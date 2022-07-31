package com.example.demoselectfilestorage.util

import java.io.File

class ClearEmpty {
    companion object{
        fun rmdir(folder: File) {
            // check if folder file is a real folder
            if (folder.isDirectory()) {
                val list: Array<File> = folder.listFiles()
                if (list != null) {
                    for (i in list.indices) {
                        val tmpF: File = list[i]
                        if (tmpF.isDirectory()) {
                            rmdir(tmpF)
                        }
                        tmpF.delete()
                    }
                }
                if (!folder.delete()) {
                    println("can't delete folder : $folder")
                }else{
                    println("Delete folder success : $folder")
                }
            }
        }
    }
}