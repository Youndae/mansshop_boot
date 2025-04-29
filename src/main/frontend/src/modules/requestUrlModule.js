//요청 QueryParam 동적 생성 처리 Module

//페이지네이션만 처리하는 Parameter 요청
export const createPageParam = (page) => {
    return createPageAndKeyword(page, null, '?');
}

//페이지네이션과 검색을 같이 처리하는 Prameter 요청
export const createPageAndKeywordUrl = (page, keyword) => {
    return createPageAndKeyword(page, keyword, '?');
}

//검색 타입이 포함될 수 있는 Parameter 생성
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

//목록 타입이 포함되는 검색, 페이지네이션 Parameter 생성
export const createListTypePageAndKeyword = (page, keyword, listType) => {
    return`?type=${listType}${createPageAndKeyword(page, keyword, '&')}`;
}

//검색 타입은 없이 검색어와 페이지네이션을 포함하는 Parameter 생성
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