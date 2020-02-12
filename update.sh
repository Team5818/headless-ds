#!/bin/bash

git pull
rm /var/www/html/index.php
cp index.php /var/www/html/index.php

systemctl restart apache2.service
systemctl restart avahi-daemon.service
systemctl restart headless-ds.service
