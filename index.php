<?php
// Web status page/interface
// FQFN: /var/www/html/index.php

echo '<title>Headless DriverStation Web Interface</title>';

$svc_name = "headless-ds";
echo '<b><pre>Headless DriverStation Web Interface</pre></b>';

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

echo '<a href="index.php?restart"><button>Restart</button></a> ';
echo '<a href="index.php?start"><button>Start</button></a> ';
echo '<a href="index.php?stop"><button>Stop</button></a>';

if(isset($_GET["restart"])) {
	exec('sudo systemctl restart ' . $svc_name . '.service', $void);
} else if(isset($_GET["start"])) {
	exec('sudo systemctl start ' . $svc_name . '.service', $void);
} else if(isset($_GET["stop"])) {
	exec('sudo systemctl stop ' . $svc_name . '.service', $void);
} else {
	exit();
}

header("refresh:0.5;url=index.php");
?>
