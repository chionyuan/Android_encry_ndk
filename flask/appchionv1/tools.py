import hashlib
import random
import base64

def base64_en(str):
    return base64.b64encode(str.encode()).decode()

def base64_de(str):
    return base64.b64decode(str).decode()

def get_token_with_id():
    str_random = ''
    for i in range(16):
        str_random += random.choice('0123456789abcdefghijklmnopqrstuvwxyz')
    # return base64_en(f'{aid}|{str_random}')
    return base64_en(str_random)


if __name__ == '__main__':
    print(get_token_with_id('abcderft'))
    print(base64_de('YWJjZGVyZnR8MGxnaWdzNG9jZ2VqbTFsbQ=='))