#!/bin/bash

if [[ $(/usr/bin/id -u) -ne 0 ]]; then
    echo "error: you cannot perform this operation unless you are root."
    exit
fi

apt-get update
apt-get install -y avahi-daemon net-tools libnss-mdns info install-info tshark apache2 php policykit-1 libapache2-mod-dnssd

systemctl enable apache2.service
systemctl enable avahi-daemon.service

echo "www-data ALL = NOPASSWD: /bin/systemctl" >> /etc/sudoers

echo "LoadModule dnssd_module /usr/lib/apache2/modules/mod_dnssd.so" >> /etc/apache2/apache2.conf
echo "DNSSDEnable on" >> /etc/apache2/apache2.conf
rm /var/www/html/index.html
cp index.php /var/www/html/index.php
echo "headless-ds" > /etc/hostname

apt-get upgrade -y
ln -s "$(pwd)"/headless-ds.py /home/frcuser/headless-ds.py
ln -s "$(pwd)"/headless-ds.service /lib/systemd/system/headless-ds.service
ln -s "$(pwd)"/index.php /var/www/html/index.php
ln -s "$(pwd)"/team.py /usr/bin/team
ln -s /sbin/ifconfig /usr/bin/ifconfig

systemctl start headless-ds.service
systemctl enable headless-ds.service

rm /DietPi/dietpi/func/dietpi-banner
ln -s "$(pwd)"/dietpi-banner /DietPi/dietpi/func/dietpi-banner
