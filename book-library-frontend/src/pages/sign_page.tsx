import React, { useState } from 'react';
import axios from 'axios';
import {useRouter} from "next/router";


export default function SignPage() {
    const router = useRouter();
    const [name, setName] = useState('');
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [result, setResult] = useState('');

    const handleRegister = async () => {
        const res = await axios.post(`http://localhost:8081/adduser`, {name, email, password});
        setResult(res.data)
    };
    return (
        <div>
            <input value={name} onChange={e => setName(e.target.value)} />
            <input value={email} onChange={e => setEmail(e.target.value)} />
            <input value={password} onChange={e => setPassword(e.target.value)} />
            <button onClick={handleRegister}>sign-in</button>
            <button onClick={() => router.push('/page')}>back to home page</button>

        </div>
    );

}

