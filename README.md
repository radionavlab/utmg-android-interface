# utmg-android-interface

UT Machine Games Controller for quadcopter trajectory input by user.

## Authors
* Nidhi Rathod
* Siddarth Kaki

## Dependencies
* VICON package

  EITHER:
  * CURRENTLY SUPPORTED - `vicon_bridge` ROS package adapted from ETH Zurich: https://github.com/radionavlab/vicon_bridge
  
  OR:
  * SUPPORT TO BE IMPLEMENTED - `vicon`/`vicon_odom` ROS packages adapted from KumarRobotics: https://github.com/radionavlab/vicon

## Setup

* Set ROS variables regarding network in each terminal used (or alternatively add these lines to `~/.bashrc`), where `192.168.1.XX` is your system's network IP address

    * run: `export ROS_HOSTNAME=192.168.1.XX`
    * run: `export ROS_MASTER_URI=http://192.168.1.XX:11311`


* Configure ROS launch files to listen to VICON on the network, and to listen to the proper topic for each quadcopter

    If using `vicon_bridge`:
    * [TODO]

    If using `vicon`/`vicon_odom`:
    * [TODO]

## Usage

1. In a new terminal, run: `roscore`
2. Start a VICON node

    If using `vicon_bridge`:
    * In a new terminal, run: `roslaunch vicon_bridge vicon.launch`
    
    If using `vicon`/`vicon_odom`:
    * In a new terminal, run: `roslaunch vicon vicon.launch`
    * In a new terminal, run: `roslaunch vicon_odom vicon_android_app.launch`
3. Start app on tablet
    * Enter `http://192.168.1.XX:11311` into app's prompt for main computer's network address
    
#### Icon Credit
Icon adapted from [Adbul Karim from Noun Project](https://thenounproject.com/term/quadcopter/1054422/) under the [Creative Commons Attribution 3.0 Unported License](https://creativecommons.org/licenses/by/3.0/).
