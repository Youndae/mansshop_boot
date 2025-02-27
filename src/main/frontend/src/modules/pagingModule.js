
export const mainProductPagingObject = (pageNum, totalPages) => {

    return createPagingObject(checkPageNum(pageNum), 10, totalPages);
}

export const productDetailPagingObject = (pageNum, totalPages) => {
    return createPagingObject(checkPageNum(pageNum), 5, totalPages);
}

const createPagingObject = (pageNum, elementSize, totalPages) => {
    let endPage = Number(Math.ceil(pageNum / elementSize) * elementSize);
    const startPage = endPage - (elementSize - 1);
    if(totalPages < endPage)
        endPage = totalPages;

    const prev = startPage > 1;
    const next = endPage < totalPages;

    return {
        startPage: startPage,
        endPage: endPage,
        prev: prev,
        next: next,
        activeNo: pageNum
    }
}

const checkPageNum = (pageNum) => {
    return pageNum === null ? 1 : pageNum
}

export const getClickNumber = (e) => {
    return e.target.textContent;
}

export const getPrevNumber = (pagingData) => {
    return pagingData.startPage - 1;
}

export const getNextNumber = (pagingData) => {
    return pagingData.endPage + 1;
}

export const pageSubmit = (page, navigate) => {
    navigate(`?page=${page}`);
}

export const searchSubmit = (keyword, navigate) => {
    navigate(`?keyword=${keyword}`);
}

export const typePageSubmit = (type, page, navigate) => {
    navigate(`?type=${type}&page=${page}`);
}

export const searchTypeSubmit = (type, keyword, navigate) => {
    navigate(`?type=${type}&keyword=${keyword}`);
}

export const searchPageSubmit = (keyword, page, navigate) => {
    navigate(`?keyword=${keyword}&page=${page}`);
}

export const searchTypePageSubmit = (type, keyword, page, navigate) => {
    navigate(`?type=${type}&keyword=${keyword}&page=${page}`);
}