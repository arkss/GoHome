from .base import *

DEBUG = False
ALLOWED_HOSTS = ['*']

WSGI_APPLICATION = 'config.wsgi.deploy.application'