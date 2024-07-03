import React, {useEffect, useState} from 'react';

import { axiosInstance } from "../../../modules/customAxios";

import MainContent from "../../ui/MainContent";
import {useDispatch, useSelector} from "react-redux";
import {setMemberObject} from "../../../modules/loginModule";

function Best() {
    const loginStatus = useSelector((state) => state.member.loginStatus);
    const [data, setData] = useState([]);
    const dispatch = useDispatch();

    useEffect(() => {
        getBestData();
    }, []);

    const getBestData = async() => {
        console.log('best axios ');

        await axiosInstance.get('main/')
            .then(res => {
                console.log('best res : ', res);
                setData(res.data.content);

                const member = setMemberObject(res, loginStatus);

                if(member !== undefined)
                    dispatch(member);

            })
            .catch(err => {
                console.log('best page error : ', err);
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