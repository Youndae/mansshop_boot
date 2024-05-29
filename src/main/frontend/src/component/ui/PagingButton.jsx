import React from 'react';
import styled from "styled-components";

const PagingButtonWrapper = styled.button`
    background: none;
    border: none;
    color: #17a2b8;
`

const PagingActiveButtonWrapper = styled.button`
    background: none;
    border: none;
    color: black;
`

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