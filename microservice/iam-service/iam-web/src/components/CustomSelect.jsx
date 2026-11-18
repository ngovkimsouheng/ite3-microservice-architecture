import React, { useState, useRef, useEffect } from 'react';
import { ChevronDown, Check } from 'lucide-react';

export default function CustomSelect({
    value,
    onChange,
    options = [],
    placeholder = 'Select an option',
    className = '',
    rounded = 'rounded-xl',
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
        const handleKeyDown = (e) => {
            if (e.key === 'Escape' && open) {
                setOpen(false);
            }
        };
        document.addEventListener('mousedown', handleClickOutside);
        document.addEventListener('keydown', handleKeyDown);
        return () => {
            document.removeEventListener('mousedown', handleClickOutside);
            document.removeEventListener('keydown', handleKeyDown);
        };
    }, [open]);

    return (
        <div className={`relative ${className}`} ref={dropdownRef}>
            <button
                type="button"
                disabled={disabled}
                onClick={() => setOpen(!open)}
                className={`w-full flex items-center justify-between px-3.5 py-2.5 ${rounded} border transition-all cursor-pointer text-left ${
                    open
                        ? 'border-[#253C95] ring-2 ring-[#253C95]/15 bg-white shadow-xs'
                        : 'border-slate-200 bg-white hover:border-slate-300'
                } ${disabled ? 'opacity-50 cursor-not-allowed bg-slate-50' : ''}`}
            >
                <div className="flex items-center gap-2.5 min-w-0 pr-2">
                    {selectedOption?.icon && (
                        <span className="shrink-0 flex items-center">{selectedOption.icon}</span>
                    )}
                    {selectedOption?.badge && (
                        <span className="text-xs font-bold font-mono px-1.5 py-0.5 rounded bg-slate-100 text-slate-700 uppercase shrink-0">
                            {selectedOption.badge}
                        </span>
                    )}
                    <span className="truncate text-sm font-semibold text-slate-800">
                        {selectedOption ? selectedOption.label : placeholder}
                    </span>
                </div>
                <ChevronDown
                    size={17}
                    className={`text-slate-400 shrink-0 transition-transform duration-200 ml-1.5 ${
                        open ? 'rotate-180 text-[#253C95]' : ''
                    }`}
                />
            </button>

            {open && (
                <div className="absolute left-0 right-0 top-full mt-1.5 bg-white rounded-xl shadow-xl shadow-slate-200/90 border border-slate-200/90 p-1.5 z-50 max-h-64 overflow-y-auto animate-in fade-in zoom-in-95 duration-150">
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
                                className={`w-full flex items-center justify-between px-3 py-2.5 rounded-lg text-sm transition-colors cursor-pointer text-left ${
                                    isSelected
                                        ? 'bg-blue-50/70 text-[#253C95] font-bold'
                                        : 'text-slate-700 hover:bg-slate-50 hover:text-slate-900 font-medium'
                                }`}
                            >
                                <div className="flex items-center gap-2.5 min-w-0">
                                    {opt.icon && <span className="shrink-0 flex items-center">{opt.icon}</span>}
                                    {opt.badge && (
                                        <span className={`text-[11px] font-bold font-mono px-1.5 py-0.5 rounded uppercase ${
                                            isSelected ? 'bg-blue-100/70 text-[#253C95]' : 'bg-slate-100 text-slate-600'
                                        }`}>
                                            {opt.badge}
                                        </span>
                                    )}
                                    <div className="min-w-0">
                                        <span className={`block truncate ${isSelected ? 'text-[#253C95]' : 'text-slate-800'}`}>
                                            {opt.label}
                                        </span>
                                        {opt.description && (
                                            <span className="block text-xs text-slate-400 font-normal truncate mt-0.5">
                                                {opt.description}
                                            </span>
                                        )}
                                    </div>
                                </div>
                                {isSelected && (
                                    <Check size={16} className="text-[#253C95] shrink-0 ml-2" strokeWidth={2.5} />
                                )}
                            </button>
                        );
                    })}
                </div>
            )}
        </div>
    );
}
