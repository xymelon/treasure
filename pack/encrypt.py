# -*- coding: utf-8 -*-
#!/usr/bin/env python

import os
import shutil

suffix_encrypted = '.apk'
suffix_normal = '_test.apk'

#市场包目录
market_dir = './market/'
#union包目录
union_dir = './union/'
#加固包目录
encrypted_dir = './encrypted/'

#移动加固包并改名
def move(dir):
    print("start move: "+ dir)
    if not os.path.exists(dir):
        print(dir + "not exits")
        return
    files = os.listdir(dir)
    for file in files:
        if not os.path.isdir(file):
            if file.endswith(suffix_encrypted):
                newFile = file.replace(suffix_encrypted, suffix_normal)
                print("move " + dir + file + " to " + encrypted_dir + newFile)
                shutil.move(dir + file, encrypted_dir + newFile)
    
#清空加固包目录
def clear_encrypted_dir():
    if os.path.exists(encrypted_dir):
        shutil.rmtree(encrypted_dir)
    os.mkdir(encrypted_dir)
    
if __name__=="__main__":
    print("clear encrypted dir")
    clear_encrypted_dir()
    move(market_dir)
    move(union_dir)
