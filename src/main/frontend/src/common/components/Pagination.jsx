import React from 'react';
import styled from 'styled-components';

import { PAGINATION_TYPE } from '../constants/paginationTypes';

const PagingLiWrapper = styled.li`
	list-style: none;
	float: left;
	padding: 10px;
`

// Pagination Form
function Pagination(props) {
    const { pagingData, handlePageBtn, className } = props;
    let prevElem = null;
    let nextElem = null;
    const pagingNumberArr = [];

    if(pagingData.endPage > 1) {
        for(let i = pagingData.startPage; i <= pagingData.endPage; i++) {
            let pagingClassName = 'pagingNumber';

            if(i === Number(pagingData.activeNo))
                pagingClassName += ' active';

            const body = {
                pageNum: i,
                className: pagingClassName,
            }

            pagingNumberArr.push(body);
        }
    }

    if(pagingData.prev) {
        prevElem =
            <PagingLiWrapper>
                <PagingButton
                    btnText={'prev'}
                    className={'pagingPrev'}
                    onClick={() => handlePageBtn(PAGINATION_TYPE.PREV)}
                />
            </PagingLiWrapper>;
    }

    if(pagingData.next) {
        nextElem =
            <PagingLiWrapper>
                <PagingButton
                    btnText={'next'}
                    className={'pagingNext'}
                    onClick={() => handlePageBtn(PAGINATION_TYPE.NEXT)}
                />
            </PagingLiWrapper>;
    }

    return (
        <div className={`paging ${className}`}>
            <ul>
                {prevElem}
                {pagingNumberArr.map((pagingNum, index) => {
                    return(
                        <PagingNumber
                            key={index}
                            pagingNumberData={pagingNum}
                            btnOnClick={() => handlePageBtn(pagingNum.pageNum)}
                        />
                    )
                })}
                {nextElem}
            </ul>
        </div>
    )
}

function PagingNumber(props) {
    const { pagingNumberData, btnOnClick } = props;

    return (
        <PagingLiWrapper>
            <PagingButton
                btnText={pagingNumberData.pageNum}
                className={pagingNumberData.className}
                onClick={btnOnClick}
            />
        </PagingLiWrapper>
    )
}

const PagingButtonBase = styled.button`
	background: none;
	border: none;
	cursor: pointer;
`

const PagingButtonWrapper = styled(PagingButtonBase)`
	color: #17a2b8;
`

const PagingActiveButtonWrapper = styled(PagingButtonBase)`
	color: black;
`

function PagingButton(props) {
    const { btnText, className, onClick } = props;

    if(className.indexOf('active') !== -1) {
        return (
            <PagingActiveButtonWrapper
                className={className}
                onClick={onClick}
                disabled={true}
            >
                {btnText}
            </PagingActiveButtonWrapper>
        )
    }else {
        return (
            <PagingButtonWrapper
                className={className}
                onClick={onClick}
            >
                {btnText}
            </PagingButtonWrapper>
        )
    }
}

export default Pagination;