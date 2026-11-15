import React, { useState, useRef, useEffect } from 'react';
import { ChevronDown, Check } from 'lucide-react';

export default function CustomSelect({
    value,
    onChange,
    options = [],
    placeholder = 'Select an option',
    className = '',
    disabled = false
}) {
    const [open, setOpen] = useState(false);
    const dropdownRef = useRef(null);

    const selectedOption = options.find(opt => String(opt.value) === String(value));

    useEffect(() => {
        const handleClickOutside = (e) => {
            if (dropdownRef.current && !dropdownRef.current.contains(e.target)) {
                setOpen(false);
            }
        };
        document.addEventListener('mousedown', handleClickOutside);
        return () => document.removeEventListener('mousedown', handleClickOutside);
    }, []);

    return (
        <div className={`relative ${className}`} ref={dropdownRef}>
            <button
                type="button"
                disabled={disabled}
                onClick={() => setOpen(!open)}
                className={`w-full flex items-center justify-between px-4 py-2.5 rounded-2xl border transition-all cursor-pointer text-left ${
                    open
                        ? 'border-[#253C95] ring-2 ring-[#253C95]/15 bg-white shadow-xs'
                        : 'border-slate-200 bg-white hover:border-slate-300'
                } ${disabled ? 'opacity-50 cursor-not-allowed bg-slate-50' : ''}`}
            >
                <div className="flex items-center gap-3 min-w-0">
                    {selectedOption?.icon && (
                        <span className="shrink-0 flex items-center">{selectedOption.icon}</span>
                    )}
                    {selectedOption?.badge && (
                        <span className="text-xs sm:text-sm font-bold font-mono text-slate-600 uppercase">
                            {selectedOption.badge}
                        </span>
                    )}
                    <span className="truncate text-sm sm:text-base font-medium text-slate-800">
                        {selectedOption ? selectedOption.label : placeholder}
                    </span>
                </div>
                <ChevronDown
                    size={18}
                    className={`text-slate-400 shrink-0 transition-transform duration-200 ml-2 ${
                        open ? 'rotate-180 text-[#253C95]' : ''
                    }`}
                />
            </button>

            {open && (
                <div className="absolute left-0 right-0 top-full mt-2 bg-white rounded-2xl shadow-xl shadow-slate-200/80 border border-slate-100 p-2 z-50 max-h-64 overflow-y-auto custom-scrollbar animate-in fade-in zoom-in-95 duration-150">
                    {options.map((opt) => {
                        const isSelected = String(opt.value) === String(value);
                        return (
                            <button
                                key={opt.value}
                                type="button"
                                onClick={() => {
                                    onChange(opt.value);
                                    setOpen(false);
                                }}
                                className={`w-full flex items-center justify-between px-3.5 py-2.5 rounded-xl text-sm sm:text-base font-medium transition-colors cursor-pointer text-left ${
                                    isSelected
                                        ? 'bg-slate-100/90 text-slate-900 font-bold'
                                        : 'text-slate-700 hover:bg-slate-50 hover:text-slate-900'
                                }`}
                            >
                                <div className="flex items-center gap-3 min-w-0">
                                    {opt.badge && (
                                        <span className={`text-xs sm:text-sm font-bold font-mono uppercase ${
                                            isSelected ? 'text-[#253C95]' : 'text-slate-500'
                                        }`}>
                                            {opt.badge}
                                        </span>
                                    )}
                                    {opt.icon && <span className="shrink-0 flex items-center">{opt.icon}</span>}
                                    <span className={`truncate ${isSelected ? 'text-[#253C95]' : ''}`}>
                                        {opt.label}
                                    </span>
                                </div>
                                {isSelected && (
                                    <Check size={18} className="text-[#253C95] shrink-0 ml-2" strokeWidth={2.5} />
                                )}
                            </button>
                        );
                    })}
                </div>
            )}
        </div>
    );
}
