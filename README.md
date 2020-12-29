# Android_encry_ndk
结合一些大型app厂商的签名算法，提供的一套思路。
aes+md5(非标准) 可有效增加签名层面计算的难度。安全性提高更多。
再编译的时候去除导出函数名字。使得ida静态分析混乱。无法肉眼可看。


# 致谢
https://github.com/kokke/tiny-AES-c
