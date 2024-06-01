
export const mainProductPagingObject = (pageNum, totalPages) => {
    return createPagingObject(pageNum, 12, totalPages);
}

export const productDetailPagingObject = (pageNum, totalPages) => {
    return createPagingObject(pageNum, 10, totalPages);
}

const createPagingObject = (pageNum, elementSize,totalPages) => {
    let endPage = Number(Math.ceil(pageNum / elementSize) * elementSize);
    const startPage = endPage - 9;
    if(totalPages < endPage)
        endPage = totalPages;

    const prev = startPage > 1;
    const next = endPage < totalPages;

    return {
        startPage: startPage,
        endPage: endPage,
        prev: prev,
        next: next,
    }
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