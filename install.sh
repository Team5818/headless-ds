#!/bin/bash

if [[ $(/usr/bin/id -u) -ne 0 ]]; then
  echo "error: you cannot perform this operation unless you are root."
  exit
fi

if [ ! -d .git]; then
  echo "fatal: Not a git repository (or any of the parent directories): .git"
  exit
fi

apt-get update
apt-get install -y avahi-daemon net-tools libnss-mdns info install-info tshark apache2 php policykit-1 libapache2-mod-dnssd

usermod -l frcuser -d /home/frcuser -m dietpi

systemctl enable apache2.service
systemctl start apache2.service
systemctl enable avahi-daemon.service
systemctl start avahi-daemon.service

echo "www-data ALL = NOPASSWD: /bin/systemctl" >> /etc/sudoers
echo "www-data ALL = (ALL:ALL) ALL" >> /etc/sudoers

echo "LoadModule dnssd_module /usr/lib/apache2/modules/mod_dnssd.so" >> /etc/apache2/apache2.conf
echo "DNSSDEnable on" >> /etc/apache2/apache2.conf
echo "headless-ds" > /etc/hostname

apt-get upgrade -y

rm /lib/systemd/system/headless-ds.service
rm /usr/bin/team
rm /var/www/html/index.php
rm /var/www/html/README.md
rm /DietPi/dietpi/func/dietpi-banner

ln -s "$(pwd)"/headless-ds.service /lib/systemd/system/headless-ds.service
ln -s "$(pwd)"/team.py /usr/bin/team
ln -s "$(pwd)"/index.php /var/www/html/index.php
ln -s "$(pwd)"/README.md /var/www/html/README.md
ln -s "$(pwd)"/ab-logo.png /var/www/html/ab-logo.png
ln -s "$(pwd)"/dietpi-banner /DietPi/dietpi/func/dietpi-banner
ln -s /sbin/ifconfig /usr/bin/ifconfig

chown -R www-data "$(pwd)"
chmod -R g+s "$(pwd)"

systemctl enable headless-ds.service
systemctl start headless-ds.service
