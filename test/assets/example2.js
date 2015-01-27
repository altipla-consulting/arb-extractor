'use strict';

//noinspection JSUnresolvedVariable,JSUnresolvedFunction
/** @desc Simple message with no placeholders */
var MSG_SIMPLE = goog.getMsg('Simple message');

/** @desc Message with plurals */
var MSG_PLURAL = goog.getMsg('{NIGHTS, plural, =1 {1 noche} other {{NIGHTS} noches}}');
