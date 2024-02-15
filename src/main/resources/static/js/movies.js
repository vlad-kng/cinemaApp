

document.addEventListener('DOMContentLoaded', function () {
    const content = document.querySelector('.content');
    const items = Array.from(content.getElementsByClassName('single-movie-container'));
    const itemsPerPage = 24;
    let currentPage;
    let searchText = window.location.search;
    if(searchText.includes("&continue")) searchText.replace("&continue", "");
    if (searchText.includes("page=")) {
        currentPage = searchText.replace("?page=", "")}
    else currentPage=0;
    document.addEventListener('hashchange', function (){
        if (window.location.search.includes("page=")) {
            currentPage = window.location.search.replace("?page=", "")
        }
    });

    function showPage(page) {
        const startIndex = page * itemsPerPage;
        const endIndex = startIndex + itemsPerPage;
        items.forEach((item, index) => {
            item.classList.toggle('hidden', index < startIndex || index >= endIndex);
        });
        updateActiveButtonStates();
        updateURL();
    }

    function createPageButtons() {
        const totalPages = Math.ceil(items.length / itemsPerPage);
        const paginationContainer = document.createElement('div');
        const paginationDiv = document.body.appendChild(paginationContainer);
        paginationContainer.classList.add('pagination');

        // Add page buttons
        for (let i = 0; i < totalPages; i++) {
            const pageButton = document.createElement('button');
            pageButton.textContent = i + 1;
            pageButton.addEventListener('click', () => {
                currentPage = i;
                showPage(currentPage);
                updateActiveButtonStates();
            });

            content.appendChild(paginationContainer);
            paginationDiv.appendChild(pageButton);
        }
    }

    function updateActiveButtonStates() {
        const pageButtons = document.querySelectorAll('.pagination button');
        pageButtons.forEach((button, index) => {
            if (index === currentPage) {
                button.classList.add('active');
            } else {
                button.classList.remove('active');
            }
        });
    }
    function updateURL() {
        if (history.pushState) {
            var baseUrl = window.location.protocol + "//" + window.location.host + window.location.pathname;
            var newUrl = baseUrl + '?page=' + (currentPage);
            history.pushState(null, null, newUrl);
        }
        else {
            console.warn('History API не поддерживается');
        }
    }

    createPageButtons(); // Call this function to create the page buttons initially
    showPage(currentPage);
});
