'use strict';

/** @desc Simple message with no placeholders */
var MSG_SIMPLE = goog.getMsg('Simple message');

/** @desc Message with a placeholder for the bold word */
var MSG_PLACEHOLDERS = goog.getMsg('Message with {$startBold}placeholders{$endBold}', {
    startBold: '<b>',
    endBold: '</b>',
});

/** @desc Message with plurals and a placeholder */
var MSG_PLURALS = goog.getMsg('{PEOPLE, plural, ' +
        '=0 {no {ATTR} people},' +
        '=1 {one {ATTR} person},' +
        'other{# people with not attr}}');

var translated = goog.i18n.MessageFormat(MSG_PLURALS, {
    ATTR: 'example',
    PEOPLE: 7,
});
