'use strict';

//noinspection JSUnresolvedVariable,JSUnresolvedFunction
/** @desc Simple message with no placeholders */
var MSG_SIMPLE = goog.getMsg('Simple message');

//noinspection JSUnresolvedVariable,JSUnresolvedFunction
/** @desc Message with a placeholder for the bold word */
var MSG_PLACEHOLDERS = goog.getMsg('Message with {$startBold}placeholders{$endBold}', {
    startBold: '<b>',
    endBold: '</b>',
});

//noinspection JSUnresolvedVariable,JSUnresolvedFunction
/** @desc Message with plurals and a placeholder */
var MSG_PLURALS = goog.getMsg('{PEOPLE, plural, ' +
        '=0 {no {ATTR} people},' +
        '=1 {one {ATTR} person},' +
        'other{# people with not attr}}');

//noinspection JSUnresolvedFunction,JSUnresolvedVariable
var translated = goog.i18n.MessageFormat(MSG_PLURALS, {
    ATTR: 'example',
    PEOPLE: 7,
});
