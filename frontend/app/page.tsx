"use client";

import { useState, useEffect, useCallback } from "react";
import FormRow from "@/components/FormRow";

const COURSES = [
  { value: "smart-phonics", label: "스마트파닉스", courseType: 1 },
  { value: "travel-english", label: "여행영어", courseType: 2 },
  { value: "free-talking", label: "프리토킹", courseType: 3 },
];

const API_BASE = process.env.NEXT_PUBLIC_API_BASE;

type TimeSlot = {
  time: string;
  available: boolean;
  assignedTutorId: string | null;
};

type SubmitResult =
  | { type: "success"; tutorId: string; endDate: string; lessonCount: number }
  | { type: "error"; message: string };

function getDateString(daysFromNow: number) {
  const date = new Date();
  date.setDate(date.getDate() + daysFromNow);
  return date.toISOString().split("T")[0];
}

export default function EnrollmentPage() {
  const [course, setCourse] = useState("travel-english");
  const [daySchedule, setDaySchedule] = useState("mon-wed-fri");
  const [startDate, setStartDate] = useState("");
  const [selectedTime, setSelectedTime] = useState<string | null>(null);
  const [timeSlots, setTimeSlots] = useState<TimeSlot[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [submitting, setSubmitting] = useState(false);
  const [result, setResult] = useState<SubmitResult | null>(null);

  const loadTimeSlots = useCallback(async () => {
    if (!startDate) {
      setTimeSlots([]);
      setError(null);
      return;
    }

    const dayPattern = daySchedule === "mon-wed-fri" ? 1 : 2;

    setLoading(true);
    setError(null);

    try {
      const res = await fetch(
        `${API_BASE}/api/attendance/available-times?dayPattern=${dayPattern}&startDate=${startDate}`
      );
      const data = await res.json();
      if (!res.ok) {
        throw new Error(data.message ?? "조회에 실패했습니다");
      }
      setTimeSlots(data);
    } catch (e) {
      setError((e as Error).message);
      setTimeSlots([]);
    } finally {
      setLoading(false);
    }
  }, [daySchedule, startDate]);

  useEffect(() => {
    setSelectedTime(null);
    loadTimeSlots();
  }, [loadTimeSlots]);

  const minDate = getDateString(0);     // 오늘
  const maxDate = getDateString(365);   // 1년 후

  const canSubmit = startDate !== "" && selectedTime !== null;

  const handleSubmit = async () => {
    const dayPattern = daySchedule === "mon-wed-fri" ? 1 : 2;
    const courseType = COURSES.find((c) => c.value === course)!.courseType;

    setSubmitting(true);
    setResult(null);

    try {
      const res = await fetch(`${API_BASE}/api/attendance`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
          studentId: "stu100",
          studentSeq: 9999,
          startDate,
          dayPattern,
          courseType,
          startTime: selectedTime,
        }),
      });

      const data = await res.json();

      if (!res.ok) {
        throw new Error(data.message ?? "신청에 실패했습니다");
      }

      setResult({
        type: "success",
        tutorId: data.assignedTutorId,
        endDate: data.endDate,
        lessonCount: data.lessonCount,
      });
      setSelectedTime(null);
      loadTimeSlots();
    } catch (e) {
      setResult({ type: "error", message: (e as Error).message });
    } finally {
      setSubmitting(false);
    }
  };


  const handleCancel = () => {
    setCourse("travel-english");
    setDaySchedule("mon-wed-fri");
    setStartDate("");
    setSelectedTime(null);
    setResult(null);
  };

  const inputClass = "min-w-[220px] px-3 py-2 border border-gray-400 rounded bg-white text-sm";

  return (
    <main className="max-w-5xl mx-auto px-6 py-10 bg-white text-gray-900">
      <h1 className="text-2xl font-bold mb-3">화상영어 수강신청</h1>
      <hr className="border-gray-300 mb-4" />
      <p className="text-sky-500 text-sm mb-8">※ 첫 수업은 레벨테스트로 진행됩니다.</p>

      <div className="border border-gray-300 w-[920px] max-w-full">
        <FormRow label="과정">
          <div className="flex items-center gap-2">
            <select
              value={course}
              onChange={(e) => setCourse(e.target.value)}
              className={inputClass}
            >
              {COURSES.map((c) => (
                <option key={c.value} value={c.value}>
                  {c.label}
                </option>
              ))}
            </select>
            <span className="text-sm text-gray-700 ml-1">&lt;과정보기&gt;</span>
          </div>
          <p className="text-sky-500 text-xs mt-2">
            ※ 수업과정은 강사님의 추천과정에 의해 변경 될 수 있습니다.
          </p>
        </FormRow>

        <FormRow label="수업요일">
          <select
            value={daySchedule}
            onChange={(e) => setDaySchedule(e.target.value)}
            className={inputClass}
          >
            <option value="mon-wed-fri">월 수 금</option>
            <option value="tue-thu">화 목</option>
          </select>
        </FormRow>

        <FormRow label="수업 시작일">
          <input
            type="date"
            value={startDate}
            onChange={(e) => setStartDate(e.target.value)}
            min={minDate}
            max={maxDate}
            className={inputClass}
          />
          <p className="text-xs text-gray-500 mt-2">
            오늘부터 1년 후까지 선택 가능합니다.
          </p>
        </FormRow>

        <FormRow label="수업시간 선택" last>
          {!startDate ? (
            <p className="text-sm text-gray-500">수업 시작일을 먼저 선택해주세요.</p>
          ) : loading ? (
            <p className="text-sm text-gray-500">가능 시간을 조회하고 있습니다...</p>
          ) : error ? (
            <p className="text-sm text-red-500">{error}</p>
          ) : (
            <div className="inline-grid grid-cols-[repeat(8,90px)] border-l border-t border-gray-300">
              {timeSlots.map((slot) => {
                const isUnavailable = !slot.available;
                const isSelected = selectedTime === slot.time;
                return (
                  <button
                    key={slot.time}
                    type="button"
                    disabled={isUnavailable}
                    onClick={() => setSelectedTime(slot.time)}
                    className={`py-3 text-sm text-center border-r border-b border-gray-300 ${
                      isUnavailable
                        ? "bg-white text-gray-500 line-through cursor-not-allowed"
                        : isSelected
                          ? "bg-amber-500 text-white cursor-pointer"
                          : "bg-white text-amber-500 hover:bg-amber-50 cursor-pointer"
                    }`}
                  >
                    {slot.time}
                  </button>
                );
              })}
            </div>
          )}
        </FormRow>
      </div>

      <div className="flex justify-center gap-6 mt-10">
        <button
          type="button"
          onClick={handleSubmit}
          disabled={!canSubmit || submitting}
          className="bg-sky-500 text-white px-16 py-3 rounded font-semibold hover:bg-sky-600 cursor-pointer disabled:bg-gray-300 disabled:cursor-not-allowed disabled:hover:bg-gray-300"
        >
          {submitting ? "신청 중..." : "신 청"}
        </button>
        <button
          type="button"
          onClick={handleCancel}
          className="bg-gray-400 text-white px-16 py-3 rounded font-semibold hover:bg-gray-500 cursor-pointer"
        >
          취 소
        </button>
      </div>

      {result && (
        <div
          className="fixed inset-0 z-50 flex items-center justify-center bg-black/40"
          onClick={() => setResult(null)}
        >
          <div
            className="mx-4 w-full max-w-sm rounded-lg bg-white p-6 text-center shadow-xl"
            onClick={(e) => e.stopPropagation()}
          >
            {result.type === "success" ? (
              <>
                <p className="text-lg font-semibold text-sky-600">신청 완료</p>
                <div className="mx-auto mt-4 w-fit space-y-2 text-sm text-gray-700">
                  <div className="flex">
                    <span className="w-16 text-left text-gray-500">배정 강사</span>
                    <span className="px-2 text-gray-400">:</span>
                    <span className="font-medium text-gray-800">{result.tutorId}</span>
                  </div>
                  <div className="flex">
                    <span className="w-16 text-left text-gray-500">수업 기간</span>
                    <span className="px-2 text-gray-400">:</span>
                    <span className="text-gray-800">{startDate} ~ {result.endDate}</span>
                  </div>
                  <div className="flex">
                    <span className="w-16 text-left text-gray-500">총 수업</span>
                    <span className="px-2 text-gray-400">:</span>
                    <span className="text-gray-800">{result.lessonCount}회</span>
                  </div>
                </div>
              </>
            ) : (
              <>
                <p className="text-lg font-semibold text-red-500">신청 실패</p>
                <p className="mt-3 text-sm text-gray-700">{result.message}</p>
              </>
            )}
            <button
              type="button"
              onClick={() => setResult(null)}
              className="mt-6 rounded bg-sky-500 px-8 py-2 text-sm font-semibold text-white hover:bg-sky-600 cursor-pointer"
            >
              확인
            </button>
          </div>
        </div>
      )}
    </main>
  );
}
