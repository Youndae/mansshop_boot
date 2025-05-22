import React from 'react';
import styled from 'styled-components';

const ButtonWrapper = styled.button`
	background-color: white;
	border: 1px solid lightgray;
	cursor: pointer;
`;


// 기본 Button UI
function DefaultButton(props) {
    const { btnText, className, onClick, id, name, value } = props;

    return (
        <ButtonWrapper
            id={id}
            name={name}
            type={'button'}
            className={className}
            onClick={onClick}
            value={value}
        >
            {btnText}
        </ButtonWrapper>
    )
}

export default DefaultButton;