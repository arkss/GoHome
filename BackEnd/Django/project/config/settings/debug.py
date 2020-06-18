from .base import *

DEBUG = True
# TODO: 나중에 꺼주기
ALLOWED_HOSTS = ['*']

WSGI_APPLICATION = 'config.wsgi.debug.application'
