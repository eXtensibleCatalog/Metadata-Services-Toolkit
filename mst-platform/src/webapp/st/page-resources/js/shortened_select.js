$(window).load(function(){
// Shorten select option text if it stretches beyond width of select element
$.each($('.shortenedSelect option'), function(key, optionElement) {
    var curText = $(optionElement).text();

    // remove any whitespace before and/or after
    curText = curText.replace(/(\r\n|\n|\r)/gm,"");
    curText = curText.replace(/^\s+|\s+$/g,"");

    $(this).attr('title', curText);

    // about 1/6th of the pixel width seems about right
    var lengthToShortenTo = parseInt(parseInt($(this).parent('select').css('width')) / 6.0 + 0.5);
    
    // truncate only those which are necessary
    if (curText.length > lengthToShortenTo) {
        var firstPart = parseInt(0.7 * lengthToShortenTo + 0.5);  // 70%/30% beginning/end
        var secondPart = lengthToShortenTo - firstPart;
        var displayThis = curText.substring(0, firstPart) + '…' + curText.substring(curText.length-secondPart);
        $(this).text(displayThis);
    }
});

// Show full name in tooltip after choosing an option
$('.shortenedSelect').change(function() {
    $(this).attr('title', ($(this).find('option:eq('+$(this).get(0).selectedIndex +')').attr('title')));
});

});
