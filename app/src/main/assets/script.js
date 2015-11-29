var solved = false;

function drawData() {
    document.getElementById('question').innerHTML = "<b>" + data.id + "</b> " + data.question;

    //randomize order options
    var resultOptions = [];

    while(data.options.length > 0) {
        var pos = Math.floor(Math.random() * data.options.length);
        resultOptions.push(data.options[pos]);
        data.options.splice(pos, 1);
    }

    data.options = resultOptions;

    //draw options
    var ulOptions = document.getElementById('options');
    var li = '';
    var solutionIndices = '';
    for(var i = 0; i < data.options.length; i++) {
        li += '<li class="option" onclick="selectOption(this, ' + i + ')">';
        li += data.options[i].text;
        li += '</li>';

        if(data.options[i].correct == true) {
            solutionIndices += i + ',';
        }
    }
    ulOptions.innerHTML = li;

    //save solution indices
    if(solutionIndices.charAt(solutionIndices.length - 1) == ',') {
        solutionIndices = solutionIndices.substring(0, solutionIndices.length - 1);
    }
    document.getElementById('solution-container').dataset.index = solutionIndices;

    //draw solution
    document.getElementById('solution').innerHTML = data.solution;
}

function selectOption(e) {
	if(solved == true) {
		return;
	}

	var selected = e.dataset.selected;
	if( selected == 'true' ) {
		e.style.backgroundColor = '#EEE';
		e.dataset.selected = 'false';
    } else {
    	e.style.backgroundColor = '#FDD54A';
    	e.dataset.selected = 'true';
    }
}

function showSolution() {
	if( solved == false ) {
        checkSolution();
        var div = document.getElementById('solution-container');
        var currentPosition = currentYPosition();
        div.style.display = "block";
        window.scrollTo(0, currentPosition);

        setTimeout("smoothScroll('solution-container')", 100);
        solved = true;
    }
}

function checkSolution() {
    var solIndexStr = document.getElementById('solution-container').dataset.index;
    var solIndex = solIndexStr.split(',');
    var solMsg = document.getElementById('solutionMsg');

    var candidateOptions = document.getElementsByTagName('li');
    var options = [];
    for (var i = 0; i < candidateOptions.length; i++) {
    	if (candidateOptions[i].className == 'option') {
    		options.push(candidateOptions[i]);
    	}
    }

    var correct = true;

    for (var i = 0; i < options.length; i++) {
    	var isSelected = (options[i].dataset.selected == 'true');
    	var isCorrectSol = false;
    	for(var j = 0; j < solIndex.length; j++) {
    		if(solIndex[j] == i) {
    			isCorrectSol = true;
    		}
    	}

    	if( (isSelected && !isCorrectSol)
    			|| (!isSelected && isCorrectSol)) {
    		correct = false;
    	}
    }

    if(correct == true) {
        solMsg.innerHTML = "OK";
        solMsg.style.backgroundColor = 'green';
        if(window.jsInterface) {
        	window.jsInterface.check(true);
        }
    } else {
        solMsg.innerHTML = "BAD";
        solMsg.style.backgroundColor = 'red';
        if(window.jsInterface) {
        	window.jsInterface.check(false);
        }
    }
}

function currentYPosition() {
    // Firefox, Chrome, Opera, Safari
    if (self.pageYOffset) return self.pageYOffset;
    // Internet Explorer 6 - standards mode
    if (document.documentElement && document.documentElement.scrollTop)
        return document.documentElement.scrollTop;
    // Internet Explorer 6, 7 and 8
    if (document.body.scrollTop) return document.body.scrollTop;
    return 0;
}


function elmYPosition(eID) {
    var elm = document.getElementById(eID);
    var y = elm.offsetTop;
    var node = elm;
    while (node.offsetParent && node.offsetParent != document.body) {
        node = node.offsetParent;
        y += node.offsetTop;
    } return y;
}


function smoothScroll(eID) {
    var startY = currentYPosition();
    var stopY = elmYPosition(eID);
    var distance = stopY > startY ? stopY - startY : startY - stopY;
    if (distance < 50) {
        scrollTo(0, stopY); return;
    }
    var speed = Math.round(distance / 50);
    if (speed >= 20) speed = 20;
    var step = Math.round(distance / 25);
    var leapY = stopY > startY ? startY + step : startY - step;
    var timer = 0;
    if (stopY > startY) {
        for ( var i=startY; i<stopY; i+=step ) {
            setTimeout("window.scrollTo(0, "+leapY+")", timer * speed);
            leapY += step; if (leapY > stopY) leapY = stopY; timer++;
        } return;
    }
    for ( var i=startY; i>stopY; i-=step ) {
        setTimeout("window.scrollTo(0, "+leapY+")", timer * speed);
        leapY -= step; if (leapY < stopY) leapY = stopY; timer++;
    }
}
