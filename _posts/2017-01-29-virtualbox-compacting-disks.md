---
title: "VirtualBox: Compacting Disks"
---

VirtualBox's dynamically allocated disks are great because they start
small, and only stretch when they need to. Thing is, they don't shrink
back *automagically* once the space is freed, they require a little
push. This is what VirtualBox calls *compacting*, and it's a great way
to trim your machines so they only use as much space as they actually
need.

In order to shrink back a disk file, it has to be **defragmented** (if
necessary), and its empty space **filled with zeroes**.

Windows has a built-in defragmenter, and the [SDelete][1] tool can be
used to fill unused space with zeroes. GNU/Linux users have a similar
tool: *zerofree*, it should be available from your distribution's
repositories.

Once the disk is ready, we use the [VBoxManage][2] tool to get the job
done:

`$ VBoxManage modifymedium --compact <uuid | filename>`

Here, we use the `modifymedium` command to compact the disk identified
either by its [UUID][3] or its filename. 

To get a list of disk files (and their respective UUIDs) available in
your computer, you can execute the following command:

`$ VBoxManage list hdds`

`modifymedium` is also capable of expanding your disks' virtual size, in
case the machine needs much more space than the initial limit provided,
using the `--resize <megabytes>` option.

[1]: https://technet.microsoft.com/en-us/sysinternals/sdelete.aspx
[2]: https://www.virtualbox.org/manual/ch08.html
[3]: https://en.wikipedia.org/wiki/Universally_unique_identifier
