<?php
// Web status page for Armabot EnableKey
// Located at /var/www/html/enable_key.php

exec('df /dev/mmcblk0p1 -H', $output);
array_push($output, "");
exec("free -h --si | grep -B1 ^Mem | tr -s ' ' | cut -d ' ' -f 1-4", $output);
array_push($output, "");
exec('ifconfig eth0', $output);
exec('systemctl status enable-key.service', $output);

echo '<pre>';
for($i = 0;$i < sizeof($output);$i++) {
	echo htmlspecialchars($output[$i]) . PHP_EOL;
}
echo '</pre>';

echo '<a href="enable_key.php?restart"><button>Restart</button></a> ';
echo '<a href="enable_key.php?start"><button>Start</button></a> ';
echo '<a href="enable_key.php?stop"><button>Stop</button></a>';

if(isset($_GET["restart"])) {
	exec('sudo systemctl restart enable-key.service', $void);
} else if(isset($_GET["start"])) {
	exec('sudo systemctl start enable-key.service 2>&1', $void);
} else if(isset($_GET["stop"])) {
	exec('sudo systemctl stop enable-key.service 2>&1', $void);
} else {
	exit();
}

header("refresh:0.5;url=enable_key.php");
?>
