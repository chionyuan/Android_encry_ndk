from flask import Flask,request
from tools import *
import requests
from mymd5 import md55
from chionaes import AESCipher

app = Flask(__name__)

def check_sign(aes_key,data:dict, request_sign:str):
    if 'chion-sign' in data.keys():
        data.pop('chion-sign')
    new_data = dict(sorted(data.items(), key=lambda x:x[0],reverse=True))
    s = ''
    for k,v in new_data.items():
        s += f'{k}={v}'
    s = s[::-1]
    des_data = AESCipher.encrypt(s,key=aes_key)
    md5_data = md55.hash(des_data.decode())
    if (md5_data == request_sign):
        return True
    else:
        return False


@app.route('/get_token',methods=['GET'])
def get_token():
    # android_id = request.args.get("android_id")
    token = get_token_with_id()
    return {
        "code":200,
        "token":token
    }

@app.route('/submit',methods=['POST'])
def submit_data():
    data = request.get_json()
    print(data)
    chion_sign = data.get('chion-sign','')
    aes_key = data.get('id','')
    check_sign_flag = check_sign(aes_key,data,chion_sign)
    if not check_sign_flag:
        return {"code":201,"error":"sign error~~"}
    return requests.get('https://v1.hitokoto.cn/').text

@app.route('/')
def hello_world():
    return '呐，做人呢，最重要的是开心，你饿不饿，我煮碗面给你吃啊'


if __name__ == '__main__':
    app.run(host='0.0.0.0',port=5001,debug=False)
