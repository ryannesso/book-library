import React, { useEffect, useState } from "react";
import axios from "axios";
import { useRouter } from "next/router";

type Book = {
    id: number;
    title: string;
    author: string;
    description: string;
    status: boolean;
    availableCopies: number;
};

type User = {
    id: number;
    name: string;
    email: string;
};

export default function Page() {
    const router = useRouter();
    const [books, setBooks] = useState<Book[]>([]);
    const [userBookIds, setUserBookIds] = useState<number[]>([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);
    const [query, setQuery] = useState("");
    const [isAuthenticated, setIsAuthenticated] = useState(false);

    useEffect(() => {
        axios
            .get<User>("http://localhost:8081/api/users/me", {
                withCredentials: true,
            })
            .then((res) => {
                if (res.data?.id) {
                    setIsAuthenticated(true);
                } else {
                    setIsAuthenticated(false);
                }
            })
            .catch(() => setIsAuthenticated(false));
    }, []);

    useEffect(() => {
        fetchBooks();
    }, []);

    useEffect(() => {
        if (!isAuthenticated) return;

        axios
            .get<Book[]>("http://localhost:8081/api/users/my_books", {
                withCredentials: true,
            })
            .then((res) => {
                const ids = res.data.map((book) => book.id);
                setUserBookIds(ids);
            })
            .catch((err) => {
                console.error("Error fetching user books:", err);
            });
    }, [isAuthenticated]);

    const fetchBooks = async () => {
        setLoading(true);
        try {
            const response = await axios.get("http://localhost:8081/api/books/all");
            setBooks(response.data);
            setError(null);
        } catch (e) {
            setError("Error loading books");
        } finally {
            setLoading(false);
        }
    };

    const handleSearch = async () => {
        if (!query.trim()) {
            fetchBooks();
            return;
        }

        setLoading(true);
        try {
            const response = await axios.get("http://localhost:8081/api/books/title", {
                params: { title: query },
            });
            setBooks(response.data);
            setError(null);
        } catch (e) {
            setError("Error during search");
        } finally {
            setLoading(false);
        }
    };

    const handleLogout = async () => {
        try {
            await axios.post(
                "http://localhost:8081/api/auth/logout",
                {},
                { withCredentials: true }
            );
            setIsAuthenticated(false);
            setBooks([]);
            router.push("/page");
        } catch (error) {
            console.error("Logout error", error);
        }
    };

    const handleBorrow = async (bookId: number) => {
        try {
            await axios.post(
                "http://localhost:8081/api/books/borrow",
                { bookId },
                { withCredentials: true }
            );
            alert("Book successfully borrowed!");
            setUserBookIds((prev) => [...prev, bookId]);
        } catch (err) {
            console.error("Error borrowing book", err);
        }
    };

    return (
        <div>
            {/* Header */}
            <div className="w-full h-[75px] bg-gray-800 flex items-center justify-between px-6">
                <h1 className="text-gray-400 text-[20px]">online library</h1>

                <div className="flex items-center gap-4">
                    <div className="bg-yellow-100 px-4 py-2 text-[22px] rounded cursor-pointer">
                        logo
                    </div>
                    <div className="bg-yellow-100 px-4 py-2 rounded cursor-pointer">
                        my books
                    </div>
                    <div className="bg-yellow-100 px-4 py-2 rounded cursor-pointer">
                        browse
                    </div>

                    {/* Search */}
                    <input
                        type="text"
                        value={query}
                        onChange={(e) => setQuery(e.target.value)}
                        onKeyDown={(e) => {
                            if (e.key === "Enter") handleSearch();
                        }}
                        placeholder="Search books..."
                        className="px-4 py-2 w-64 rounded border border-gray-300 focus:outline-none focus:ring-2 focus:ring-yellow-400"
                    />
                    <button
                        onClick={handleSearch}
                        className="bg-yellow-400 px-4 py-2 rounded text-white font-semibold"
                    >
                        Search
                    </button>

                    {/* Auth buttons */}
                    {isAuthenticated ? (
                        <>
                            <button
                                onClick={() => router.push("/profile")}
                                className="bg-green-200 px-4 py-2 text-[22px] rounded"
                            >
                                personal cabinet
                            </button>
                            <button
                                onClick={handleLogout}
                                className="bg-red-300 px-4 py-2 text-[22px] rounded"
                            >
                                logout
                            </button>
                        </>
                    ) : (
                        <>
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
                        </>
                    )}
                </div>
            </div>

            {/* Book List */}
            <div className="max-w-4xl mx-auto p-6 mt-8">
                <h2 className="text-2xl font-bold mb-4">Book list</h2>

                {loading && <p>Loading...</p>}
                {error && <p className="text-red-600">{error}</p>}
                {!loading && !error && books.length === 0 && <p>No books found</p>}

                <div className="max-h-[500px] overflow-y-auto pr-2">
                    <ul className="space-y-4">
                        {books.map((book) => (
                            <li
                                key={book.id}
                                className="border rounded p-4 shadow hover:shadow-lg transition flex justify-between items-center"
                            >
                                <div>
                                    <h3 className="text-lg font-semibold">
                                        {book.title}{" "}
                                        <span className="text-gray-500 text-sm">(ID: {book.id})</span>
                                    </h3>
                                    <p className="text-gray-700">Author: {book.author}</p>
                                    <p className="text-gray-600 mt-1">
                                        Copies available:{" "}
                                        <span className="font-semibold">{book.availableCopies}</span>
                                    </p>
                                </div>

                                {userBookIds.includes(book.id) ? (
                                    <span className="text-green-600 font-semibold">
                                        Already borrowed
                                    </span>
                                ) : (
                                    <button
                                        onClick={() => handleBorrow(book.id)}
                                        className="bg-blue-500 hover:bg-blue-600 text-white px-4 py-2 rounded"
                                    >
                                        Borrow
                                    </button>
                                )}
                            </li>
                        ))}
                    </ul>
                </div>
            </div>
        </div>
    );
}
