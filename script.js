document.addEventListener("DOMContentLoaded", function() {
    const searchBox = document.getElementById("searchBox");
    const suggestionsDiv = document.getElementById("suggestions");

    fetch('classData.csv')
        .then(response => response.text())
        .then(text => {
            const rows = text.split('\n'); // Split CSV into rows
            const suggestions = rows.map(row => {
                const columns = row.split(','); // Split row into columns
                return columns[2]; // Extract the third column (index 2)
            });

            searchBox.addEventListener("input", function() {
                const input = this.value.toLowerCase();

                const matchedSuggestions = suggestions.filter(suggestion => {
                    const words = suggestion.split(' ');
                    return words.length > 1 && words[1].toLowerCase().includes(input);
            });

                if (input === ''){
                    suggestionsDiv.innerHTML = '';
                    return;
                }
                const sortedSuggestions = matchedSuggestions.slice(0, 5).sort();
                displaySuggestions(sortedSuggestions);
            });

            suggestionsDiv.addEventListener("click", function(event) {
                if (event.target && event.target.nodeName === "DIV") {
                    searchBox.value = event.target.textContent;
                    suggestionsDiv.innerHTML = ''; // Clear suggestions
                }
            });
        })
        .catch(error => {
            console.error('Error fetching suggestions:', error);
        });


        function displaySuggestions(suggestions) {
            suggestionsDiv.innerHTML = ''; // Clear previous suggestions
            suggestions.forEach((suggestion, index) => {
                if (index < 5) {
                    const suggestionElem = document.createElement("div");
                    suggestionElem.textContent = suggestion;
                    suggestionsDiv.appendChild(suggestionElem);
                }
            });
        }
    });