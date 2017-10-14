# utmg-android-interface

UT Machine Games Controller for quadcopter trajectory input by user. This branch is for the app rearchitecture.

## Dependencies
* VICON package

  EITHER:
  * CURRENTLY SUPPORTED - `vicon_bridge` ROS package adapted from ETH Zurich: https://github.com/radionavlab/vicon_bridge
  
  OR:
  * SUPPORT TO BE IMPLEMENTED - `vicon`/`vicon_odom` ROS packages adapted from KumarRobotics: https://github.com/radionavlab/vicon
  
* Path Planning Service

    * `mav_trajectory_generation` ROS package adapted from ETH Zurich: https://github.com/marcelinomalmeidan/mav_trajectory_generation

## Setup

* Set ROS variables regarding network in each terminal used (or alternatively add these lines to `~/.bashrc`), where `192.168.1.XX` is your system's network IP address

    * run: `export ROS_HOSTNAME=192.168.1.XX`
    * run: `export ROS_MASTER_URI=http://192.168.1.XX:11311`


* Configure ROS launch files to listen to VICON on the network, and to listen to the proper topic for each quadcopter

    If using `vicon_bridge`:
    * Edit `~/catkin_ws/src/vicon_bridge/launch/vicon.launch` to reflect IP address of the VICON host computer (the `datastream_hostport` parameter should be set to `192.168.1.100:801`)

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

3. If using `mav_trajectory_generation` path planning service (optional, but recommended):
    * In a new terminal, run: `rosrun mav_trajectory_generation_ros minSnap_Node`

4. Start app on tablet
    * Enter `http://192.168.1.XX:11311` into app's prompt for main computer's network address
    
#### Icon Credit
Icon adapted from [Adbul Karim from Noun Project](https://thenounproject.com/term/quadcopter/1054422/) under the [Creative Commons Attribution 3.0 Unported License](https://creativecommons.org/licenses/by/3.0/).
