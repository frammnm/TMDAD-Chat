const metricPosition = {
    wordCount: 0,
    senderScore: 1,
    receiverScore: 2
}

function handleMetricReceived(metric){
    console.log(metric);
    let wordCount = metric[metricPosition.wordCount];
    let senderScore = metric[metricPosition.wordCount];
    let receiverScore = metric[metricPosition.wordCount];

    //[
    // {a=0.7071067811865474, b=-1.4142135623730951, c=0.7071067811865474},
    // {francisco=1.3363062095621219, juan=-0.2672612419124245, roberto=-1.0690449676496978},
    // {testapi2=1.3363062095621219, test2=-1.0690449676496978, grupopruebazuzu3afterupdate=-0.2672612419124245}
    // ]
}

$(function () {
    var ctx1 = document.getElementById('wordCount').getContext('2d');
    var ctx2 = document.getElementById('senderScore').getContext('2d');
    var ctx3 = document.getElementById('receiverScore').getContext('2d');
    var wordCount = new Chart(ctx1, {
        type: 'bar',
        data: {
            labels: ['Red', 'Blue', 'Yellow', 'Green', 'Purple', 'Orange'],
            datasets: [{
                label: '# of Votes',
                data: [12, 19, 3, 5, 2, 3],
                backgroundColor: [
                    'rgba(255, 99, 132, 0.2)',
                    'rgba(54, 162, 235, 0.2)',
                    'rgba(255, 206, 86, 0.2)',
                    'rgba(75, 192, 192, 0.2)',
                    'rgba(153, 102, 255, 0.2)',
                    'rgba(255, 159, 64, 0.2)'
                ],
                borderColor: [
                    'rgba(255, 99, 132, 1)',
                    'rgba(54, 162, 235, 1)',
                    'rgba(255, 206, 86, 1)',
                    'rgba(75, 192, 192, 1)',
                    'rgba(153, 102, 255, 1)',
                    'rgba(255, 159, 64, 1)'
                ],
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
    var senderScore = new Chart(ctx2, {
        // The type of chart we want to create
        type: 'line',

        // The data for our dataset
        data: {
            labels: ['January', 'February', 'March', 'April', 'May', 'June', 'July'],
            datasets: [{
                label: 'My First dataset',
                backgroundColor: 'rgb(255, 99, 132)',
                borderColor: 'rgb(255, 99, 132)',
                data: [0, 10, 5, 2, 20, 30, 45],
                fill: false
            }]
        },

        // Configuration options go here
        options: {}
    });
    var receiverScore = new Chart(ctx3, {
        // The type of chart we want to create
        type: 'line',

        // The data for our dataset
        data: {
            labels: ['January', 'February', 'March', 'April', 'May', 'June', 'July'],
            datasets: [{
                label: 'My First dataset',
                backgroundColor: 'rgb(255, 99, 132)',
                borderColor: 'rgb(255, 99, 132)',
                data: [0, 10, 5, 2, 20, 30, 45],
                fill: false
            }]
        },

        // Configuration options go here
        options: {}
    });
});