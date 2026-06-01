package com.example.data

import kotlinx.coroutines.flow.Flow

class MemberRepository(private val memberDao: MemberDao) {
    val allMembers: Flow<List<MemberEntity>> = memberDao.getAllMembers()

    fun getMembersBySubCommunity(subCommunity: String): Flow<List<MemberEntity>> {
        return memberDao.getMembersBySubCommunity(subCommunity)
    }

    suspend fun insertMember(member: MemberEntity) {
        memberDao.insertMember(member)
    }

    suspend fun deleteMember(member: MemberEntity) {
        memberDao.deleteMember(member)
    }

    suspend fun deleteMemberById(id: Int) {
        memberDao.deleteMemberById(id)
    }
}
