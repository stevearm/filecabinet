function (head, req) {
    var row, duplicates = [];
    while (row = getRow()) {
        if (row.value > 1) {
            duplicates.push(row);
        }
    }
    send(JSON.stringify(duplicates));
}
