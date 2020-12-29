import base64
from Crypto.Cipher import AES

class AESCipher(object):

    def __init__(self):
        self.bs = AES.block_size
        self.iv = "1234567890abcdef".encode()

    def pkcs7padding(self, text):
        """明文使用PKCS7填充 """
        bs = 16
        length = len(text)
        bytes_length = len(text.encode('utf-8'))
        padding_size = length if (bytes_length == length) else bytes_length
        padding = bs - padding_size % bs
        padding_text = chr(padding) * padding
        self.coding = chr(padding)
        return text + padding_text

    def encrypt(self, raw, key='!@%^&*89'):
        raw = self.pkcs7padding(raw)
        cipher = AES.new(key.encode(), AES.MODE_CBC, self.iv)
        return base64.b64encode(cipher.encrypt(raw.encode()))

    def decrypt(self, enc, key='!@%^&*89'):
        enc = base64.b64decode(enc)
        cipher = AES.new(key.encode(), AES.MODE_CBC, self.iv)
        return cipher.decrypt(enc).decode()

    def _pad(self, s):
        return s + (self.bs - len(s) % self.bs) * chr(self.bs - len(s) % self.bs)

    @staticmethod
    def _unpad(s):
        return s[:-ord(s[len(s)-1:])]

AESCipher = AESCipher()
