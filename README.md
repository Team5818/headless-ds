# EnableKey
Enables FRC robots on ethernet connection

## OrangePi Zero Installation
1. Download [Armbian](https://dl.armbian.com/orangepizero/Ubuntu_bionic_next.7z)
2. Unzip `.img` file
3. Plug in microSD card
4. Figure out what device the SD card is by running `lsblk -p`. The device name is the one that will automatically mount to `/media/`. Note the *drive* device name, not the partition one (`/dev/mmcblk0` instead of `dev/mmcblk0p1`)
5. (Optional) Only if the card has mounted: Unmount the SD card by using the eject in the file explorer or running `sudo umount <partitionName>`. Repeat for as many partitions as have been mounted.
6. Mount `.img` to SD using `sudo dd bs=4M if=<filepathToArmbianImg> of=<deviceName> conv=fsync status=progress`
7. Connect OrangePi to ethernet and put in SD card w/ power
8. SSH into OrangePi (default user = root) or use serial monitor (USB)
9. Set up new user as "pi" with default pw
10. Run `apt-get update` and `apt-get upgrade`
11. Move current directory into `/home/pi/` by executing `cd /home/pi/`
12. Clone in the repository using `git clone https://github.com/Team5818/EnableKey`
13. Move the python script (`enable_key.py`) out one directory to `/home/pi/` by executing `mv EnableKey/enable_key.py enable_key.py`
14. Move the systemctl service to its destination by running `mv EnableKey/enable-key.service /etc/systemd/system/enable-key.service`
15. Start the service by running `sudo systemctl start enable-key.service`
16. Have the service start on bootup by running `sudo systemctl enable enable-key.service`

## Troubleshooting
* Restart the Pi: `sudo systemctl reboot -i`
* Reload systemctl configuration: `sudo systemctl daemon-reload`
* Start/Stop/Restart/View logs (service): `sudo systemctl <start|stop|restart|status> enable-key.service`
