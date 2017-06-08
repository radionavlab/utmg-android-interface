# utmg-android-interface

UT Machine Games Android Interface for quadcopter trajectory input by user.

## Authors
* Nidhi Rathod
* Siddarth Kaki

## Dependencies
* Vicon package

  EITHER:
  * CURRENTLY SUPPORTED -`vicon_bridge` ros package adapted from ETH Zurich: https://github.com/radionavlab/vicon_bridge
  
  OR:
  * SUPPORT TO BE IMPLEMENTED - vicon ros package adapted from KumarRobotics: https://github.com/radionavlab/vicon

## Usage

1. Set ROS variables regarding network in each terminal used (or alternatively add these lines to `~/.bashrc`), where `192.168.1.XX` is your system's network IP address
    * run: `export ROS_HOSTNAME=192.168.1.XX`
    * run: `export ROS_MASTER_URI=http://192.168.1.XX:11311`
2. In a new terminal, run: `roscore`
3. Start a VICON node

    If using `vicon_bridge`:
    * In a new terminal, run: `roslaunch vicon_bridge vicon.launch`
    
    If using `vicon`:
    * In a new terminal, run: `roslaunch vicon vicon.launch`
    * In a new terminal, run: `roslaunch vicon_odom vicon_android_app.launch`
4. Start app on tablet
    * Enter `http://192.168.1.XX:11311` into app's prompt for main computer's network address
