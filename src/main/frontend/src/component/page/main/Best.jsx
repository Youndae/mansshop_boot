import React, {useEffect, useState} from 'react';
import {useDispatch, useSelector} from "react-redux";

import { axiosInstance } from "../../../modules/customAxios";
import {setMemberObject} from "../../../modules/loginModule";

import MainContent from "../../ui/MainContent";

function Best() {
    const loginStatus = useSelector((state) => state.member.loginStatus);

    const [data, setData] = useState([]);

    const dispatch = useDispatch();

    useEffect(() => {
        getBestData();
    }, []);

    const getBestData = async() => {

        await axiosInstance.get('main/')
            .then(res => {
                setData(res.data.content);

                const member = setMemberObject(res, loginStatus);

                if(member !== undefined)
                    dispatch(member);

            })
    }

    return (
        <>
            <MainContent
                data={data}
                classification={'BEST'}
            />
        </>
    )
}

export default Best;