ShiftClock
==========

A Android clock based on shift duty.

基于值班表的 Android 闹钟。


Background:
* JOB, has certain duties and schedule watches
* Duty, class of watch, has different work begin/end time in one day
* WATCH, on duty in a day

基本场景是：
* 工作岗位（Job），有 N 种固定的工作时段，有 M 个人轮流上班，有一定的休息规则
* 班种（Duty），有不同的上下班时间
* 值班（Watch），班种在某一天的的安排


Features:
* Define some DUTYs, which includes work begin time and duration in one day, time alarmed before duty and interval between alarms
* Set a day as rest or a kind of duty, formed the WATCH List
* Creating dynamic alarm according the Watch List

功能：
* 定义 N 种不同班种，定义其工作时段，并为每种工作时段定义不同的提前闹铃时间
* 设置某天是休息还是上某个班种——形成值班列表
* 根据值班列表创建上班闹铃


最后，感谢 Ricy！

