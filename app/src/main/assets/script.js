solved = false;
letters = ['A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'];


function writeData() {
    if (typeof state === "undefined") {
        state = {};
        writeInitialData();
    } else {
        writeWithStateData();
    }
}


function writeInitialData() {
    document.getElementById('question').innerHTML = "<b>" + data.id + "</b> " + data.question;


    //set position of letters in options
    for (var i = 0; i < data.options.length && i < letters.length; i++) {
        data.options[i].letterPos = i;
    }

    //put unselected state to the options
    for (var i = 0; i < data.options.length; i++) {
        data.options[i].selected = false;
    }

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
        li += '<li class="option" onclick="selectOption(this, ' + i + ')" style="margin: 10px; padding: 5px; font-family: Arial;">';
        li += '<div style="float: left; background-color: #48F; color: white; padding: 2px; margin-right: 6px; margin-bottom: 6px; width: 15px; height: 15px; text-align: center; font-weight: bold;">';
        li += letters[i];
        li += '</div>';
        li += data.options[i].text;
        li += '</li>';

        data.options[i].letter = letters[i];

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

    //replace template ${} with the custom letter
    for(var i = 0; i < data.options.length; i++) {
        var regexp = new RegExp('\\$\\{' + data.options[i].letterPos + '\\}', "g");
        data.solution = data.solution.replace(regexp, '<span style="  background-color: #48F; color: white; padding: 0 4px 0 4px; font-weight: bold;">' + data.options[i].letter + '</span>');
    }

    //draw solution
    document.getElementById('solution').innerHTML = data.solution;

    //save state;
    state.options = data.options;
    state.solved = false;
    saveState();
}

function writeWithStateData() {
    document.getElementById('question').innerHTML = "<b>" + data.id + "</b> " + data.question;

    //draw options
    var ulOptions = document.getElementById('options');
    var li = '';
    var solutionIndices = '';
    for(var i = 0; i < state.options.length; i++) {
        if (state.options[i].selected === true) {
            li += '<li class="option" onclick="selectOption(this, ' + i + ')" style="margin: 10px; padding: 5px; font-family: Arial; background-color: #FDD54A;">';
        } else {
            li += '<li class="option" onclick="selectOption(this, ' + i + ')" style="margin: 10px; padding: 5px; font-family: Arial;">';
        }
        li += '<div style="float: left; background-color: #48F; color: white; padding: 2px; margin-right: 6px; margin-bottom: 6px; width: 15px; height: 15px; text-align: center; font-weight: bold;">';
        li += letters[i];
        li += '</div>';
        li += state.options[i].text;
        li += '</li>';

        if(state.options[i].correct == true) {
            solutionIndices += i + ',';
        }
    }
    ulOptions.innerHTML = li;

    //save solution indices
    if(solutionIndices.charAt(solutionIndices.length - 1) == ',') {
        solutionIndices = solutionIndices.substring(0, solutionIndices.length - 1);
    }
    document.getElementById('solution-container').dataset.index = solutionIndices;

    //replace template ${} with the custom letter
    for(var i = 0; i < state.options.length; i++) {
        var regexp = new RegExp('\\$\\{' + state.options[i].letterPos + '\\}', "g");
        data.solution = data.solution.replace(regexp, '<span style="  background-color: #48F; color: white; padding: 0 4px 0 4px; font-weight: bold;">' + state.options[i].letter + '</span>');
    }

    //draw solution
    document.getElementById('solution').innerHTML = data.solution;

    //show solution according to the state
    if (state.solved === true) {
        showSolution(false);
    }

}

function saveState() {
    if(window.jsInterface) {
        window.jsInterface.setState(JSON.stringify(state));
    }
}

function selectOption(e, index) {
	if(solved == true) {
		return;
	}

	var selected = e.dataset.selected;
	if( selected == 'true' ) {
		e.style.backgroundColor = '#EEE';
		e.dataset.selected = 'false';
		state.options[index].selected = false;
		saveState();
    } else {
    	e.style.backgroundColor = '#FDD54A';
    	e.dataset.selected = 'true';
    	state.options[index].selected = true;
    	saveState();
    }
}

function showSolution(scroll) {
	if( solved == false ) {
        checkSolution();
        var div = document.getElementById('solution-container');
        var currentPosition = currentYPosition();
        div.style.display = "block";

        if (scroll === true) {
            window.scrollTo(0, currentPosition);
            setTimeout("smoothScroll('solution-container')", 100);
        }
        
        solved = true;
        state.solved = true;
        saveState();
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
    } else {
        solMsg.innerHTML = "BAD";
        solMsg.style.backgroundColor = 'red';
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
