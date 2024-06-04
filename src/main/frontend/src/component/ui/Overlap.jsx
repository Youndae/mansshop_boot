import React from 'react';
import styled from "styled-components";


const OverlapDiv = styled.div`
    color: red;
`

function Overlap(props) {
    const { overlapText } = props;

    return (
        <OverlapDiv>
            {overlapText}
        </OverlapDiv>
    )
}

export default Overlap;