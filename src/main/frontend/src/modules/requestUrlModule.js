export const createPageParam = (page) => {
    return createPageAndKeyword(page, null, '?');
}

export const createPageAndKeywordUrl = (page, keyword) => {
    return createPageAndKeyword(page, keyword, '?');
}

export const createPageAndSearchTypeKeyword = (page, keyword, searchType) => {
    let url = '';

    if(page === null && keyword !== null)
        url = `?keyword=${keyword}&searchType=${searchType}`;
    else if(page !== null && keyword !== null)
        url = `?page=${page}&keyword=${keyword}&searchType=${searchType}`;
    else if(page !== null && keyword === null)
        url = `?page=${page}`;

    return url;
}

export const createListTypePageAndKeyword = (page, keyword, listType) => {
    return`?type=${listType}${createPageAndKeyword(page, keyword, '&')}`;
}

const createPageAndKeyword = (page, keyword, prefix) => {
    if(page !== null && keyword === null)
        return `${prefix}page=${page}`;
    else if(page === null && keyword !== null)
        return `${prefix}keyword=${keyword}`;
    else if(page !== null && keyword !== null)
        return `${prefix}page=${page}&keyword=${keyword}`;
    else
        return '';
}