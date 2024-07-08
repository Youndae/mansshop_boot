import React from 'react';
import styled from "styled-components";

const Button = styled.button`
    background-color: white;
    border: 1px solid lightgrey;
    cursor: pointer;
`;

function DefaultBtn(props) {
    const { btnText, className, onClick, id, name, value, disable} = props;

    return (
        <Button
            id={id}
            name={name}
            type={'button'}
            className={className}
            onClick={onClick}
            value={value}
        >
            {btnText}
        </Button>
    )
}

export default DefaultBtn;