# Headless Driver Station
Enables FRC robots on ethernet connection. 

[Now an affordable high quality Armabot product!](https://www.armabot.com/collections/frontpage/products/Headless-ds)

##  Installation
### Instructions
1. Download [Armbian Debian Stretch](https://dl.armbian.com/orangepizero/Debian_stretch_next.7z)
2. Unzip `.img` file
3. Plug in microSD card
4. Figure out what device the SD card is by running `lsblk`. The device name is the one that will automatically mount to `/media/`. Note the *drive* device name, not the partition one (`/dev/mmcblk0` instead of `dev/mmcblk0p1`)
5. (Optional) Only if the card has mounted: Unmount the SD card by using the eject in the file explorer or running `sudo umount <partitionName>`. Repeat for as many partitions as have been mounted.
6. Mount `.img` to SD using `sudo dd bs=4M if=<filepathToArmbianImg> of=<deviceName> conv=fsync status=progress`
7. Connect OrangePi to ethernet and put in SD card w/ power
8. SSH into OrangePi (default user = root, pw = 1234) or use the serial monitor (USB)
9. Set up new user as "frcuser" with pw "admin"
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
20. Back out again and select "Network Options: Adapters" and turn off the WiFi adapter option.
21. Change the "dietpi" user to "frcuser" by running `usermod -l frcuser -d /home/frcuser -m dietpi`
22. Exit all the way out of the config and run `apt-get install avahi-daemon net-tools libnss-mdns info install-info tshark apache2 php policykit-1 libapache2-mod-dnssd`. This will install the avahi hostname daemon, ifconfig, mdns resolver, http server, and packet analyzer.
23. Enable the http server on startup by running `systemctl enable apache2.service`
24. Enable the avahi mdns resolver by executing `systemctl enable avahi-daemon.service`
25. Give root permissions to "www-data" so the http server can execute systemctl commands: `sudo visudo` or `sudo nano /etc/sudoers` and add this to the bottom: `www-data ALL = NOPASSWD: /bin/systemctl`
26. Edit the apache2 config file through nano using `nano /etc/apache2/apache2.conf` and add these two lines:
  * `LoadModule dnssd_module /usr/lib/apache2/modules/mod_dnssd.so`
  * `DNSSDEnable on`
27. Set the hostname to "headless-ds" by executing `sudo nano /etc/hostname` and changing the contents of the file to "headless-ds" (without the quotes).
28. Run `apt-get update` and `apt-get upgrade`
29. Ensure that the current directory is `/home/frcuser/` (if not change it to that using `cd /home/frcuser/`). Clone the headless-ds Git repository using `git clone https://github.com/Team5818/headless-ds.git`.
30. Remove existing files and create symlinks in their place. Run `rm <dest>` then `sudo ln -s <src> <dest>` for each of the following pairs of `<src>` `<dest>`:

  | Source `<src>` | Destination `<dest>` | Description |
  |----------------|----------------------|-------------|
  |`/home/frcuser/headless-ds/headless-ds.service` | `/lib/systemd/system/headless-ds.service` | systemctl service config |
  |`/home/frcuser/headless-ds/team.py` | `/usr/bin/team` | team number utility |
  |`/home/frcuser/headless-ds/index.php` | `/var/www/html/index.php` | web config page |
  |`/home/frcuser/headless-ds/ab-logo.png` | `/var/www/html/ab-logo.png` | Armabot logo via web config |
  |`/home/frcuser/headless-ds/dietpi-banner` | `/DietPi/dietpi/func/dietpi-banner` | ssh login banner |
  |`/sbin/ifconfig` | `/usr/bin/ifconfig` | ifconfig through frcuser |
31. Ensure that the Apache web server can access the symlinked files by changing the owner to `www-data`. Execute the following:
  * `chown -R www-data /home/frcuser/headless-ds/`
  * `chmod -R g+s /home/frcuser/headless-ds/`
32. Start the service by running `sudo systemctl start headless-ds.service`
33. Have the service start on bootup/startup by running `sudo systemctl enable headless-ds.service`

### Install Script
The included `install.sh` script will perform steps 22-33 if placed in the correct `/home/frcuser/headless-ds` folder. The Git repository should be present in its entirety before this time. Run as `sudo`.

## Updates
The "Update Device" button on the web dashboard will update the headless-ds with any new software published. The latest version will automatically be downloaded and applied. This requires an internet connection.

## Troubleshooting
* Use the web interface at `http://headless-ds.local`
* SSH into the Pi: `ssh frcuser@headless-ds.local` if on the same network and running an mdns resolver.
* Restart the Pi: `sudo systemctl reboot -i`
* Reload systemctl configuration: `sudo systemctl daemon-reload`
* Start/Stop/Restart/View logs (service): `sudo systemctl <start|stop|restart|status> headless-ds.service`
* Check the packet output with a packet analyzer (tshark, a CLI of Wireshark)
  * Run `tshark -c 100 -Y "udp"`. Check for packets directed at the RoboRIO address (typically 10.TE.AM.2) or of length 6. If you see a bunch of TCP discovery requests, the device can't find the RoboRIO but has a "correct" network configuration. If you don't see anything relevant, either the network is set up incorrectly or the packets aren't being sent for some reason.
* If the service doesn't run, try changing the "User" and "Group" in the service file to the same as the owner/user of the python script. If set up correctly, they should both be "root". If not, run `ls -la /home/frcuser/headless-ds.py`. The two names on the left should be the same as those in the service, editable by running `nano /lib/systemd/system/headless-ds.service`. Reload the systemctl configuration and restart the service to save the changes.
* If everything looks fine but no packets are being sent, `libnss-mdns` may not have been installed. To install it without having the pi connected to the internet, download it, scp it onto the pi, and use dpkg to install it. For other packages, the architecture is `armhf`, and Armbian is Debian-based, so any Debian packages built for armhf should work.
  * `wget http://http.us.debian.org/debian/pool/main/n/nss-mdns/libnss-mdns_0.10-8_armhf.deb`
  * `scp libnss-mdns_0.10-8_armhf.deb root@headless-ds.local:.`
  * `ssh root@headless-ds.local`
  * `dpkg -i libnss-mdns_0.10-8_armhf.deb`

## Making Copies
1. Insert the working SD card to a linux computer
2. (Optional) If the primary partition is larger than the SD card you want to copy to and has space left over, it should be resized before copying. To do this, use `gparted` on a GUI-based Linux distro with the partition in question unmounted. The same can be done from the command line using a combination of `resize2fs`, `e2fsck`, and `lvresize`.
3. Find the end sector of the partition desired to copy. To do this, run `fdisk -l`. Look for the partition you want, and record the number under "End". That number plus one is the number to be entered as the count parameter in the following `dd` command.
4. Run `sudo dd bs=512 if=<sdDeviceName> of=<pathToOutImgFile> status=progress count=<last sector of primary partition + 1>` to get a `.img` file, an exact copy of the leading space and primary partition with offset (the 910 MiB = 954MB partition, the main one). We want to copy the partition and the space in front of it, giving the device as much space as possible. Note that the img file is not mountable because it encompasses an offset partition. The device name can be found using steps 4 and 5 above. The path to img file can be whatever you want.
5. Remove the SD card and insert another SD card, the one you want to copy to.
6. Run the command in reverse, swapping `if` and `of`, but making sure your partitions are not mounted.
  * To unmount a partition, run `umount /dev/mmcblk0pX` as root
