import datetime
import itertools
import netifaces
import socket
import struct
import time
from typing import Tuple, Optional, Dict, Any


def read_addr() -> Optional[str]:
    ifaddresses = netifaces.ifaddresses('eth0')  # type: Dict[int, Any]
    addresses = ifaddresses.get(netifaces.AF_INET)
    if addresses is None:
        return None
    for address_set in addresses:
        brd = address_set['broadcast']  # type: str
        if not brd.startswith('10.'):
            continue
        # Extract center two components of IP address
        high, low = brd.split('.')[1:3]
        if high == '0':
            team_number = low
        else:
            team_number = high + low
        return 'roboRIO-' + team_number + '-FRC.local'


def try_conn(addr: Tuple[str, int]) -> bool:
    try:
        sock = socket.create_connection(addr, timeout=1)
        sock.close()
        return True
    except OSError:
        return False


def counter():
    yield from range(1, 2 ** 16)
    yield from itertools.cycle(range(0, 2 ** 16))


def main():
    while True:
        address = read_addr()
        if address is None:
            time.sleep(1)
            continue
        tcp_addr = (address, 22)
        if not try_conn(tcp_addr):
            continue
        print("Connected to", tcp_addr)
        header = bytearray((0x00, 0x00, 0x01, 0x00, 0x10, 0x03))
        addr = (address, 1110)
        sock = socket.socket(type=socket.SOCK_DGRAM)
        current_time = 0
        current_dtime = datetime.datetime.utcnow()
        msg = struct.pack('>i' + 'B' * 8, current_time,
                          current_dtime.second, current_dtime.minute, current_dtime.hour,
                          current_dtime.day, current_dtime.month, current_dtime.year - 1900, 0x10, 0x10)
        sock.sendto(header + b'\x0F' + msg, addr)
        for i in counter():
            if i % 10 == 0 and not try_conn(tcp_addr):
                print("Disconnected from", tcp_addr)
                break
            header[0:2] = struct.pack('>H', i)
            if i > 20:
                header[3] = 0x04;
            sock.sendto(header, addr)
            time.sleep(0.02)


if __name__ == "__main__":
    main()
