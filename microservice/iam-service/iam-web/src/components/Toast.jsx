import React, { useEffect, useState } from 'react';
import { CheckCircle2, AlertCircle, Info, X } from 'lucide-react';

export default function Toast({ alert, onClose, duration = 4500 }) {
    const [progress, setProgress] = useState(100);

    useEffect(() => {
        if (!alert) return;

        setProgress(100);
        const interval = 50;
        const step = (interval / duration) * 100;

        const timer = setInterval(() => {
            setProgress((prev) => {
                if (prev <= step) {
                    clearInterval(timer);
                    if (onClose) onClose();
                    return 0;
                }
                return prev - step;
            });
        }, interval);

        return () => clearInterval(timer);
    }, [alert, duration, onClose]);

    if (!alert) return null;

    const isSuccess = alert.type === 'success';
    const isError = alert.type === 'error';

    return (
        <div className="fixed top-6 right-6 z-50 max-w-sm w-full px-4 sm:px-0 transition-all duration-200 animate-in fade-in slide-in-from-top-3">
            <div className="bg-white/95 backdrop-blur-md border border-slate-200/90 rounded-2xl shadow-xs p-3.5 flex items-start gap-3 relative overflow-hidden">
                {/* Icon Badge */}
                <div
                    className={`w-8 h-8 rounded-xl flex items-center justify-center shrink-0 border ${
                        isSuccess
                            ? 'bg-emerald-50 text-emerald-600 border-emerald-100'
                            : isError
                            ? 'bg-red-50 text-[#DF1F28] border-red-100'
                            : 'bg-blue-50 text-[#253C95] border-blue-100'
                    }`}
                >
                    {isSuccess && <CheckCircle2 size={18} />}
                    {isError && <AlertCircle size={18} />}
                    {!isSuccess && !isError && <Info size={18} />}
                </div>

                {/* Message */}
                <div className="flex-1 pr-1 pt-0.5">
                    <p className="text-xs sm:text-sm font-semibold text-slate-800 leading-snug">
                        {alert.message}
                    </p>
                </div>

                {/* Close Button X */}
                <button
                    type="button"
                    onClick={onClose}
                    className="text-slate-400 hover:text-slate-700 hover:bg-slate-100 rounded-lg p-1 transition-all cursor-pointer shrink-0"
                    title="Close"
                    aria-label="Close toast"
                >
                    <X size={16} />
                </button>

                {/* Micro Progress Bar */}
                <div className="absolute bottom-0 left-0 right-0 h-0.5 bg-slate-100">
                    <div
                        className={`h-full transition-all duration-75 ease-linear ${
                            isSuccess
                                ? 'bg-emerald-500'
                                : isError
                                ? 'bg-[#DF1F28]'
                                : 'bg-[#253C95]'
                        }`}
                        style={{ width: `${progress}%` }}
                    />
                </div>
            </div>
        </div>
    );
}

