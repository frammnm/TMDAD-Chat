let charts = [];
const metricPosition = {
    wordScore: 0,
    senderScore: 1,
    receiverScore: 2
}

const colors = [
    'rgba(255, 99, 132, 0.2)',
    'rgba(54, 162, 235, 0.2)',
    'rgba(255, 206, 86, 0.2)',
    'rgba(75, 192, 192, 0.2)',
    'rgba(153, 102, 255, 0.2)',
    'rgba(255, 159, 64, 0.2)'
]

function handleMetricReceived(metric){
    console.log(metric);
    let wordScores = metric["wordScores"];
    let senderScores = metric["senderScores"];
    let receiverScores = metric["receiverScores"];
    //let wordCount = metric["wordCount"];
    //let sentToCount = metric["sentToCount"];
    //let sentFromCount = metric["sentFromCount"];

    updateChart(charts[metricPosition.wordScore], wordScores);
    updateChart(charts[metricPosition.senderScore], senderScores);
    updateChart(charts[metricPosition.receiverScore], receiverScores);
}

function updateChart(chart, data){
    let labels = Object.getOwnPropertyNames(data);
    let values = [];

    chart.data.labels = labels;
    labels.forEach(function (label) {
        values.push(data[label]);
    });

    //Update data
    chart.data.datasets.forEach(function(dataset) {
        dataset.data = values;
        dataset.backgroundColor = [];
        dataset.borderColor = [];

        //Change colors
        values.forEach(function(val) {
            let index = Math.floor(Math.random() * colors.length);
            dataset.backgroundColor.push(colors[index]);
            dataset.borderColor.push(colors[index]);
        });
    });

    chart.update();
}

$(function () {
    let ctx1 = document.getElementById('wordScore').getContext('2d');
    let ctx2 = document.getElementById('senderScore').getContext('2d');
    let ctx3 = document.getElementById('receiverScore').getContext('2d');
    let wordScoreChart = new Chart(ctx1, {
        type: 'bar',
        data: {
            labels: [],
            datasets: [{
                label: 'Score of words',
                data: [],
                backgroundColor: [],
                borderColor: [],
                borderWidth: 1
            }]
        },
        options: {
            scales: {
                yAxes: [{
                    ticks: {
                        beginAtZero: true
                    }
                }]
            }
        }
    });
    let senderScoreChart = new Chart(ctx2, {
        type: 'bar',
        data: {
            labels: [],
            datasets: [{
                label: 'Score of senders',
                data: [],
                backgroundColor: [],
                borderColor: [],
                borderWidth: 1
            }]
        },
        options: {
            scales: {
                yAxes: [{
                    ticks: {
                        beginAtZero: true
                    }
                }]
            }
        }
    });
    let receiverScoresChart = new Chart(ctx3, {
        type: 'bar',
        data: {
            labels: [],
            datasets: [{
                label: 'Score of receivers',
                data: [],
                backgroundColor: [],
                borderColor: [],
                borderWidth: 1
            }]
        },
        options: {
            scales: {
                yAxes: [{
                    ticks: {
                        beginAtZero: true
                    }
                }]
            }
        }
    });

    charts = [];
    charts.push(wordScoreChart, senderScoreChart, receiverScoresChart);
});