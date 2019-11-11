# EnableKey
Enables FRC robots on ethernet connection

## OrangePi Zero Installation
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
19. Exit all the way out of the config and run `apt-get install avahi-daemon net-tools`
20. Change the "dietpi" user to "pi" by running `usermod -l pi -d /home/pi -m dietpi`
21. Run `apt-get update` and `apt-get upgrade`
22. Download python file by running `curl https://githubusercontent.com/Team5818/EnableKey/master/enable_key.py --output /home/pi/enable_key.py`
23. Download systemctl service by running `curl https://githubusercontent.com/Team5818/EnableKey/master/enable-key.service --output /lib/systemd/system/enable-key.service`
24. Start the service by running `sudo systemctl start enable-key.service`
25. Have the service start on bootup by running `sudo systemctl enable enable-key.service`

## Troubleshooting
* Restart the Pi: `sudo systemctl reboot -i`
* Reload systemctl configuration: `sudo systemctl daemon-reload`
* Start/Stop/Restart/View logs (service): `sudo systemctl <start|stop|restart|status> enable-key.service`
