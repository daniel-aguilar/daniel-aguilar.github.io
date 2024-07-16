---
layout: post
title: JPQL Considered Harmful
---

In a Java project, it's not unusual to find database queries using the
Jakarta Persistence Query Language (JPQL), a fairly easy to use
language with a syntax akin to SQL. While this is a perfectly
acceptable way to declare small queries, it can quickly spiral out of
control for more advanced requirements. The Criteria API (and its
Spring Data derivative Specifications) allow for a modular,
programmatic, and type safe declaration of queries. It's so good,
you'll probably never want to go back to writing string queries again!

The data access layer is such an ubiquitous component of our
applications, one could dare to say it's the essence of it---business
logic is derived from this data after all. As a programmer you'll
spend a great deal of time in this layer, which means you've probably
run into the following piece of code:

{% highlight java %}
// Long and unformatted query...
String jpql = "FROM Store s JOIN s.guitars g WHERE g.price > " +
        "(SELECT max(g2.price) FROM Guitar g2 JOIN g2.store s2 WHERE s2 = :folsomStore) ";
if (someCondition) {
    // ...split across different conditions
    jpql += "AND g.fretboardWood NOT IN (:maple, :ebony) ";
}
jpql += "ORDER BY s.location ASC";

Query query = entityManager.createQuery(jpql);
// ... set query parameters
return query.getResultList(); // No type safety
{% endhighlight %}

In such a small example you can already spot a few code smells, not to
mention that string mangling can become extremely gruesome in most
cases. Given the amount of time we spent maintaining modules with this
particular pattern found in every method, surely there must be a
better way to build complex queries without developer experience
trade-offs.

## Criteria API

[Criteria API][1] was introduced with the release of [JSR 317: Java
Persistence API, Version 2.0][2] in 2009. Criteria queries are written
in plain Java, are type safe, and just like its JPQL (formerly Java
Persistence Query Language) counterpart, they work regardless of the
underlying data store.

Switching your plain string queries to Criteria API is, in my opinion,
the single most significant improvement you can make to your
application's data layer:

* Drastically enhance the developer experience by unlocking powerful
  programmatic control flow techniques, without sacrificing
  readability. Say goodbye to complicated string mangled spaghetti
  code.
* Reduce potentially hundreds of lines of duplicate queries with
  little to no differences by encouraging modularity and reuse. Client
  requests a change be reflected in multiple reports? Modifying a
  single query is often enough.
* Improve performance by skipping unnecessary `JOIN` clauses you might
  have to support a single (and rarely materialized) condition in
  your string appending line of `WHERE`s.

[Spring Data JPA][3] abstracts this API even further and introduces
the concept of [Specifications][4] as small building blocks which can
be combined and used with `JpaRepository` without the need to declare
a query (method) for every needed combination.

## Guitar Inventory Example

I'm not joking when I say that after using Criteria API/Specifications
you'll never want to write a single string query again. And to make
that case, I've created a sample project using the three styles of
query definitions. I'm using Spring Boot 2.7 which, while way past its
open source support window, still runs Java 8 code. This choice is
deliberate so this article can reach a much wider audience.

If you're not using Spring, but do have access to the
[EntityManager][5], then this article still applies to you. So I
encourage you to download the [source code][6] and follow along with
me!

The project consists of a guitar inventory management system for
multiple brick-and-mortar store locations:

![ER Diagram](https://i.imgur.com/KXb2FDZ.png)

### Business Logic

Suppose the client's point of sale needs to fetch available guitars
for sale based on the following rules:

1. By default, only the current store (Roseville) inventory is shown,
   but one can toggle the full catalog
2. Due to a shortage, no maple fretboards are available for sale
3. Dave is a new hire and only sells guitars priced at $100 or less

We start by creating a `getInventory` method with a signature like so:

{% highlight java %}
List<Guitar> getInventory(boolean showAllStores, int salesPersonId);
{% endhighlight %}

### Using JPQL (String queries)

Because we wouldn't want to define a new string query for *every*
single escenario (which is subject to change), the usual approach is
appending conditionals to the `WHERE` clause in the JPQL string:

{% highlight java %}
// CustomizedGuitarRepositoryImpl.java stub
List<Guitar> getInventoryUsingJPQL(boolean showAllStores, int salesPersonId) {
    boolean isNewHire = salesPersonId == DAVE;

     /*
      * I manually format the string here, but I've witnessed incredibly unreadable
      * one-liners. Plus there's a lack of tooling to standardize this.
      */
     String jpql =
             "SELECT g " + // We have to deliberately add spacing
             "FROM Guitar g " +
             "JOIN g.fretboardWood f " +
             "JOIN g.store s " + // Premature JOIN clause (what if showAllStores is true?)
             "WHERE ";

    jpql += "f.id != :maple AND ";

    if (!showAllStores) {
        jpql += "s.id = :roseville AND ";
    }

    if (isNewHire) {
        jpql += "g.price <= :priceCap AND ";
    }

    jpql += "1 = 1"; // Make sure the query does not end with AND
    
    Query query = entityManager.createQuery(jpql);
    // ... set query parameters
    return query.getResultList(); // No type safety
}
{% endhighlight %}

While the current business logic is not very complex, there's no doubt
that the previous code is unfit for more elaborate requirements. The
string mangling will quickly become unmaintainable and the lack of
modularity makes it impossible to reuse the conditions in a different
query or even expand upon it (i.e. additional filtering). On that last
note, we might be tempted to filter the query results in a different
layer of our application, like the front-end, but this means losing
the performance of the query optimizer of our database to custom
filtering code of a data structure (meaning we now have to maintain
code in two different locations).

### Using Criteria API

With Criteria API, each conditional is represented with a
[Predicate][7]. These can be manipulated programmatically, expanded
upon, and reused across our codebase:

{% highlight java %}
// CustomizedGuitarRepositoryImpl.java stub
List<Guitar> getInventoryUsingCriteria(boolean showAllStores, int salesPersonId) {
    CriteriaBuilder cb = entityManager.getCriteriaBuilder();
    CriteriaQuery<Guitar> cq = cb.createQuery(Guitar.class);
    Root<Guitar> guitar = cq.from(Guitar.class);
    boolean isNewHire = salesPersonId == DAVE;

    Predicate noMapleFretboards = cb.notEqual(guitar.get(Guitar_.FRETBOARD_WOOD).get(FretboardWood_.ID),
            FretboardWood.MAPLE);
    Predicate fromRosevilleStore = cb.equal(guitar.get(Guitar_.STORE).get(Store_.ID), Store.ROSEVILLE);
    Predicate atNewHirePriceCap = cb.lessThanOrEqualTo(guitar.get(Guitar_.PRICE), NEW_HIRE_PRICE_CAP);

    List<Predicate> predicates = new ArrayList<>();
    predicates.add(noMapleFretboards);

    if (!showAllStores) {
        predicates.add(fromRosevilleStore);
    }

    if (isNewHire) {
        predicates.add(atNewHirePriceCap);
    }

    cq = cq.select(guitar).where(toVarargs(predicates));
    TypedQuery<Guitar> query = entityManager.createQuery(cq);
    return query.getResultList();
}

Predicate[] toVarargs(List<Predicate> predicates) {
    return predicates.toArray(new Predicate[predicates.size()]);
}
{% endhighlight %}

Now you might be wondering just what the heck is this `Guitar_`
business. `Guitar_` (and `FretboardWood_`) are metamodel classes. The
metamodel class and its attributes are used in Criteria queries to
refer to the managed entity classes and their persistent state and
relationships. Metamodel classes are typically generated by annotation
processors either at development time or at runtime. A metamodel class
is created with a trailing underscore. [Hibernate Metamodel
Generator][8] is a popular annotation processor. The following is the
autogenerated metamodel class for the `Guitar` `@Entity`:

{% highlight java %}
// Guitar_.java stub
// ... package declaration and import statements

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(Guitar.class)
public abstract class Guitar_ {

	public static volatile SingularAttribute<Guitar, Integer> serialNumber;
	public static volatile SingularAttribute<Guitar, BigDecimal> price;
	public static volatile SingularAttribute<Guitar, Model> model;
	public static volatile SingularAttribute<Guitar, BodyFinish> finish;
	public static volatile SingularAttribute<Guitar, Store> store;
	public static volatile SingularAttribute<Guitar, FretboardWood> fretboardWood;

	public static final String SERIAL_NUMBER = "serialNumber";
	public static final String PRICE = "price";
	public static final String MODEL = "model";
	public static final String FINISH = "finish";
	public static final String STORE = "store";
	public static final String FRETBOARD_WOOD = "fretboardWood";

}
{% endhighlight  %}

Thanks to these generated classes, our criteria queries always have
the correct attribute names and types. They can easily be updated,
should they ever change.

### Using Spring Data JPA Specifications

The last approach involves the declaration of
specifications. [Specification][9] is a functional interface with the
following signature:

{% highlight java %}
jakarta.persistence.criteria.Predicate toPredicate(
        jakarta.persistence.criteria.Root<T> root,
        jakarta.persistence.criteria.CriteriaQuery<?> query,
        jakarta.persistence.criteria.CriteriaBuilder criteriaBuilder)
{% endhighlight %}

You typically write these as static methods in a Specs class like so:

{% highlight java %}
// GuitarSpecs.java
// ... package declaration and import statements
public class GuitarSpecs {

    public static Specification<Guitar> noMapleFretboards() {
        return (root, query, builder) -> {
            Join<Guitar, FretboardWood> fretboardWood = root.join(Guitar_.FRETBOARD_WOOD);
            return builder.notEqual(fretboardWood.get(FretboardWood_.ID), FretboardWood.MAPLE);
        };
    }

    public static Specification<Guitar> from(Store store) {
        return (root, query, builder) ->
                builder.equal(root.get(Guitar_.STORE), store);
    }

    public static Specification<Guitar> limitPriceTo(BigDecimal value) {
        return (root, query, builder) ->
                builder.lessThanOrEqualTo(root.get(Guitar_.PRICE), value);
    }
}
{% endhighlight %}

Finally, we chain these specs with `.or()` & `.and()` (as per our
requirements) into a single spec, which we then pass to an
all-too-familiar `findAll` method:

{% highlight java %}
// AppService.java stub
List<Guitar> getInventoryUsingSpecs(boolean showAllStores, int salesPersonId) {
    boolean isNewHire = salesPersonId == DAVE;

    Specification<Guitar> spec = GuitarSpecs.noMapleFretboards();

    if (!showAllStores) {
        Store roseville = storeRepository.getReferenceById(Store.ROSEVILLE);
        spec = spec.and(GuitarSpecs.from(roseville));
    }

    if (isNewHire) {
        spec = spec.and(GuitarSpecs.limitPriceTo(NEW_HIRE_PRICE_CAP));
    }

    return guitarRepository.findAll(spec);
}
{% endhighlight %}

## Conclusion

JPQL queries are a perfectly valid JPA tool and are not going away any
time soon (they're well supported in Spring Data JPA with the
[@Query][10] annotation). But a key aspect of being a professional is
choosing the right tool for the job, and the benefits of using
Criteria API/Specifications for complex querying are far too many to
be ignored in favor of string mangling. Criteria API represents an
incredibly valuable force multiplier that's available to you the
moment JPQL is.

[1]: https://javaee.github.io/tutorial/persistence-criteria.html
[2]: https://download.oracle.com/otn-pub/jcp/persistence-2.0-fr-eval-oth-JSpec/persistence-2_0-final-spec.pdf
[3]: https://docs.spring.io/spring-data/jpa/docs/2.7.18/reference/html/
[4]: https://docs.spring.io/spring-data/jpa/docs/2.7.18/reference/html/#specifications
[5]: https://javaee.github.io/javaee-spec/javadocs/javax/persistence/EntityManager.html
[6]: https://github.com/daniel-aguilar/daniel-aguilar.github.io/tree/master/samples/jpa-criteria
[7]: https://javaee.github.io/javaee-spec/javadocs/javax/persistence/criteria/Predicate.html
[8]: https://hibernate.org/orm/tooling
[9]: https://docs.spring.io/spring-data/jpa/docs/2.7.18/api/org/springframework/data/jpa/domain/Specification.html
[10]: https://docs.spring.io/spring-data/jpa/docs/2.7.18/api/org/springframework/data/jpa/repository/Query.html
