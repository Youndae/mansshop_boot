import React, {useState, useEffect} from 'react';
import {useDispatch, useSelector} from "react-redux";

import { axiosInstance } from "../../../modules/customAxios";
import {setMemberObject} from "../../../modules/loginModule";

import MainContent from "../../ui/MainContent";

function New() {
    const loginStatus = useSelector((state) => state.member.loginStatus);

    const [data, setData] = useState([]);

    const dispatch = useDispatch();

    useEffect(() => {
        getNewData();
    }, []);

    const getNewData = async() => {

        await axiosInstance.get(`main/new`)
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
                classification={'NEW'}
            />
        </>
    )
}

export default New;