import React from 'react';
import styled from "styled-components";

const PagingButtonWrapper = styled.button`
    background: none;
    border: none;
    color: #17a2b8;
    cursor: pointer;
`

const PagingActiveButtonWrapper = styled.button`
    background: none;
    border: none;
    color: black;
    cursor: pointer;
`

// 페이지네이션의 버튼 폼
function PagingButton(props) {
    const {btnText, onClick, className} = props;

    if(className.indexOf('active') !== -1){
        return (
            <PagingActiveButtonWrapper className={className} onClick={onClick}>
                {btnText}
            </PagingActiveButtonWrapper>
        )
    }else {
        return (
            <PagingButtonWrapper className={className} onClick={onClick}>
                {btnText}
            </PagingButtonWrapper>
        )
    }
}

export default PagingButton;