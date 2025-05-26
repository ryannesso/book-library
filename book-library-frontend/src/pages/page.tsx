import React, { useEffect, useState } from "react";
import axios from "axios";
import { useRouter } from "next/router";

type Book = {
    id: number;
    title: string;
    author: string;
};

export default function Page() {
    const router = useRouter();
    const [books, setBooks] = useState<Book[]>([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);

    useEffect(() => {
        async function fetchBooks() {
            try {
                const response = await axios.get("http://localhost:8081/api/books/all");
                setBooks(response.data);
            } catch (e) {
                setError("Ошибка загрузки книг");
            } finally {
                setLoading(false);
            }
        }
        fetchBooks();
    }, []);

    return (
        <div>
            {/* Шапка */}
            <div className="w-full h-[75px] bg-gray-800 flex items-center justify-between px-6">
                {/* Заголовок */}
                <h1 className="text-gray-400 text-[20px]">online library</h1>

                {/* Навигация */}
                <div className="flex items-center gap-4">
                    <div className="bg-yellow-100 px-4 py-2 text-[22px] rounded">logo</div>
                    <div className="bg-yellow-100 px-4 py-2 rounded">my books</div>
                    <div className="bg-yellow-100 px-4 py-2 rounded">browse</div>

                    {/* Поисковая строка */}
                    <input
                        type="text"
                        placeholder="Search books..."
                        className="px-4 py-2 w-64 rounded border border-gray-300 focus:outline-none focus:ring-2 focus:ring-yellow-400"
                    />

                    {/* Кнопки логина */}
                    <button
                        onClick={() => router.push("/login_page")}
                        className="bg-yellow-100 px-4 py-2 text-[22px] rounded"
                    >
                        log-in
                    </button>
                    <button
                        onClick={() => router.push("/sign_page")}
                        className="bg-yellow-100 px-4 py-2 text-[22px] rounded"
                    >
                        sign-in
                    </button>
                </div>
            </div>

            {/* Блок со списком книг */}
            <div className="max-w-4xl mx-auto p-6 mt-8">
                <h2 className="text-2xl font-bold mb-4">Список книг</h2>

                {loading && <p>Загрузка...</p>}
                {error && <p className="text-red-600">{error}</p>}

                {!loading && !error && books.length === 0 && <p>Книг пока нет.</p>}

                <ul className="space-y-4">
                    {books.map((book) => (
                        <li
                            key={book.id}
                            className="border rounded p-4 shadow hover:shadow-lg transition"
                        >
                            <h3 className="text-lg font-semibold">{book.title}</h3>
                            <p className="text-gray-700">Автор: {book.author}</p>
                        </li>
                    ))}
                </ul>
            </div>
        </div>
    );
}
