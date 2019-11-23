# EnableKey
Enables FRC robots on ethernet connection

##  Installation
1. Download [Armbian Debian Stretch](https://dl.armbian.com/orangepizero/Debian_stretch_next.7z)
2. Unzip `.img` file
3. Plug in microSD card
4. Figure out what device the SD card is by running `lsblk -p`. The device name is the one that will automatically mount to `/media/`. Note the *drive* device name, not the partition one (`/dev/mmcblk0` instead of `dev/mmcblk0p1`)
5. (Optional) Only if the card has mounted: Unmount the SD card by using the eject in the file explorer or running `sudo umount <partitionName>`. Repeat for as many partitions as have been mounted.
6. Mount `.img` to SD using `sudo dd bs=4M if=<filepathToArmbianImg> of=<deviceName> conv=fsync status=progress`
7. Connect OrangePi to ethernet and put in SD card w/ power
8. SSH into OrangePi (default user = root, pw = 1234) or use serial monitor (USB)
9. Set up new user as "pi" with default pw
10. Install DietPi over the Armbian install using [these](https://github.com/MichaIng/DietPi/issues/1285#issue-280771944) instructions. Restart as necessary.
11. SSH into the OrangePi again (default user = root, pw = dietpi)
12. Set the new unix password to the default pw and continue installer until config screen comes up.
13. Search for "pip" and "git client" packages and install those
14. Change the default SSH client to OpenSSH
15. Proceed by selecting "Install" and selecting "Opt Out" when prompted for the survey. Wifi is not needed, nor is the serial port.
16. SSH back into the system after reboot and run `dietpi-config`
17. Scroll to "advanced options" then "Swapfile". Press "OK" when prompted.
18. Select `/dev/mmcblk0p1` or equivalent and enter "0" for the swapfile value
19. Go back out to "Network Options: Misc" and press "Boot Net Wait". Select "0: Disabled" and press OK
20. Exit all the way out of the config and run `apt-get install avahi-daemon net-tools libnss-mdns info install-info tshark apache2 php policykit-1 libapache2-mod-dnssd`. This will install the avahi hostname daemon, ifconfig, mdns resolver, http server, and packet analyzer.
21. Change the "dietpi" user to "pi" by running `usermod -l pi -d /home/pi -m dietpi`
22. Enable the http server on startup by running `systemctl enable apache2.service`
23. Enable the avahi mdns resolver by executing `systemctl enable avahi-daemon.service`
24. Give root permissions to "www-data" so the http server can execute systemctl commands: `sudo visudo` and add this to the bottom: `www-data ALL = NOPASSWD: /bin/systemctl`
25. Edit the apache2 config file through nano using `nano /etc/apache2/apache2.conf` and add these two lines:
  * `LoadModule dnssd_module /usr/lib/apache2/modules/mod_dnssd.so`
  * `DNSSDEnable on`
26. Remove the default apache file: `rm /var/www/html/index.html`
27. Set the hostname to "EnableKey" by executing `sudo nano /etc/hostname` and changing the contents of the file to "EnableKey" (without the quotes).
28. Run `apt-get update` and `apt-get upgrade`
29. Download python file by running `curl https://raw.githubusercontent.com/Team5818/EnableKey/master/enable_key.py --output /home/pi/enable_key.py`
30. Download the systemctl service by running `curl https://raw.githubusercontent.com/Team5818/EnableKey/master/enable-key.service --output /lib/systemd/system/enable-key.service`
31. Download the php page by running `curl https://raw.githubusercontent.com/Team5818/EnableKey/master/index.php --output /var/www/html/index.php`
32. Start the service by running `sudo systemctl start enable-key.service`
33. Have the service start on bootup/startup by running `sudo systemctl enable enable-key.service`

## Troubleshooting
* Use the web interface at `enablekey.local`
* SSH into the Pi: `ssh root@EnableKey.local` if on the same network and running an mdns resolver.
* Restart the Pi: `sudo systemctl reboot -i`
* Reload systemctl configuration: `sudo systemctl daemon-reload`
* Start/Stop/Restart/View logs (service): `sudo systemctl <start|stop|restart|status> enable-key.service`
* If the service doesn't run, try changing the "User" and "Group" in the service file to the same as the owner/user of the python script. If set up correctly, they should both be "root". If not, run `ls -la /home/pi/enable_key.py`. The two names on the left should be the same as those in the service, editable by running `nano /lib/systemd/system/enable-key.service`. Reload the systemctl configuration and restart the service to save the changes.
* If everything looks fine but no packets are being sent, `libnss-mdns` may not have been installed. To install it without having the pi connected to the internet, download it, scp it onto the pi, and use dpkg to install it.
  * `wget http://http.us.debian.org/debian/pool/main/n/nss-mdns/libnss-mdns_0.10-8_armhf.deb`
  * `scp libnss-mdns_0.10-8_armhf.deb root@EnableKey.local:.`
  * `ssh root@EnableKey.local`
  * `dpkg -i libnss-mdns_0.10-8_armhf.deb`

## Making Copies
1. Insert the working SD card to a linux computer
2. Run `sudo dd bs=4M if=<primaryPartitionName> of=<pathToOutImgFile>` to get a `.img` file, an exact copy of the primary partition (the 900 MiB = 944MB partition, the main one). The device name can be found using steps 4 and 5 above. The path to img file can be whatever you want.
3. Remove the SD card and insert another SD card, the one you want to copy to.
4. Run the command in reverse, swapping `if` and `of`, but making sure your partitions are not mounted.
