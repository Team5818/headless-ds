<?php
// Web status page/interface
// FQFN: /var/www/html/index.php

echo '<title>Headless DriverStation Web Interface</title>';
echo '<link rel=icon href=ab-logo.png>';

$svc_name = "headless-ds";
exec("cd /home/frcuser/headless-ds/ && git describe --tags --abbrev=0", $ver);
echo '<b><pre>Headless DriverStation Web Interface | Version ' . $ver[0] . ' | FRC Team ' . exec(team) . '</pre></b>';

exec('df /dev/mmcblk0p1 -H', $output);
array_push($output, "");
exec("free -h --si | grep -B1 ^Mem | tr -s ' ' | cut -d ' ' -f 1-4", $output);
array_push($output, "");
exec('ifconfig eth0', $output);
exec('systemctl status ' . $svc_name . '.service', $output);
array_push($output, "");
exec('systemctl status avahi-daemon.service | grep ".local"', $output);


echo '<pre>';
for($i = 0;$i < sizeof($output);$i++) {
	echo htmlspecialchars($output[$i]) . PHP_EOL;
}
echo '</pre>';

echo 'Service: ';
echo '<a href="index.php?restart"><button>Restart</button></a> ';
echo '<a href="index.php?start"><button>Start</button></a> ';
echo '<a href="index.php?stop"><button>Stop</button></a>';
echo '<br /><br />Other: ';
echo '<a href="index.php?update"><button>Update Device</button></a> ';
echo '<a href="README.md"><button>README</button></a>';

if(isset($_GET["restart"])) {
	exec('sudo systemctl restart ' . $svc_name . '.service', $void);
} else if(isset($_GET["start"])) {
	exec('sudo systemctl start ' . $svc_name . '.service', $void);
} else if(isset($_GET["stop"])) {
	exec('sudo systemctl stop ' . $svc_name . '.service', $void);
} else if(isset($_GET["update"])) {
	exec('cd /home/frcuser/headless-ds/ && git fetch --tags && git describe --tags --abbrev=0', $latestTag);
	exec('sh -c "cd /home/frcuser/headless-ds/ && git reset --hard ' . $latestTag[0] . ' && git pull && sudo systemctl daemon-reload"', $void);
} else {
	exit();
}

header("refresh:0.5;url=index.php");
?>
